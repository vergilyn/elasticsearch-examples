package com.vergilyn.examples.es.repository.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.vergilyn.examples.es.repository.AbstractDocument;
import com.vergilyn.examples.es.repository.EsCrudRepository;
import com.vergilyn.examples.es.repository.EsRepositoryExt;
import com.vergilyn.examples.es.repository.annotation.EsDocument;
import com.vergilyn.examples.es.repository.exception.EsResponseException;
import com.vergilyn.examples.es.repository.exception.ResolveException;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * id限制只支持string。
 * 参考：spring-data-elasticsearch，org.springframework.data.elasticsearch.repository.support.AbstractElasticsearchRepository
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/6/23
 */
public abstract class AbstractEsCrudRepository<T extends AbstractDocument, ID extends String>  implements EsCrudRepository<T, ID>, EsRepositoryExt<T, ID> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEsCrudRepository.class);

    private static final TimeValue DEFAULT_TIMEOUT = TimeValue.timeValueMinutes(2);
    protected Class<T> entityClass;
    protected EsDocument entityDocument;

    @Autowired
    private RestHighLevelClient rhlClient;

    public AbstractEsCrudRepository() {
    }

    public AbstractEsCrudRepository(RestHighLevelClient rhlClient) {
        Assert.notNull(rhlClient, "RestHighLevelClient must not be null!");

        this.rhlClient = rhlClient;
    }

    public RestHighLevelClient getRhlClient(){
        return rhlClient;
    }

    @Override
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "Cannot save 'null' entity.");

        try {
            IndexRequest indexRequest = createIndexRequest(entity)
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
            IndexResponse resp = rhlClient.index(indexRequest);
            entity.setMetaId(resp.getId());
            entity.setMetaIndex(resp.getIndex());
            entity.setMetaType(resp.getType());
            entity.setMetaVersion(resp.getVersion());

            return entity;
        } catch (Exception e) {
            throw new EsResponseException(e.getMessage(), e);
        }
    }

    @Override
    public <S extends T> List<S> save(List<S> entities) {
        Assert.notNull(entities, "Cannot insert 'null' as a List.");
        Assert.notEmpty(entities, "Cannot insert empty List.");

        BulkRequest bulk = new BulkRequest()
                .setRefreshPolicy(WriteRequest.RefreshPolicy.NONE)
                .timeout(getTimeout());

        for (S entity : entities){
            bulk.add(createIndexRequest(entity));
        }

        try {
            BulkResponse resp = rhlClient.bulk(bulk);
            BulkItemResponse[] items = resp.getItems();
            List<S> rs = Lists.newArrayList();
            for (int i = 0, len = items.length; i < len; i++){
                BulkItemResponse item = items[i];
                rs.add(parse(item, entities.get(i)));
            }
            return rs;
        } catch (Exception e) {
            throw new EsResponseException(e.getMessage(), e);
        }
    }

    @Override
    public <S extends T> S get(ID id) {
        Assert.notNull(id, "id is null");
        try {
            EsDocument document = getEntityDocument();
            GetResponse resp = rhlClient.get(new GetRequest(document.index(), document.type(), id));
            return parse(resp);
        } catch (Exception e) {
            throw new EsResponseException(e.getMessage(), e);
        }
    }

    @Override
    public <S extends T> List<S> get(List<ID> ids) {
        if (ids == null || ids.isEmpty()){
            return null;
        }


        MultiGetRequest multi = new MultiGetRequest();
        EsDocument document = getEntityDocument();
        String index = document.index(), type = document.type();

        for (ID id : ids){
            multi.add(new MultiGetRequest.Item(index, type, id));
        }

        try {
            MultiGetResponse resp = rhlClient.multiGet(multi);
            MultiGetItemResponse[] itemResp = resp.getResponses();
            List<S> list = Lists.newArrayList();

            for(MultiGetItemResponse item : itemResp){
                S parse = parse(item);
                if (parse != null){
                    list.add(parse);
                }
            }
            return list;
        } catch (Exception e) {
            throw new EsResponseException(e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(ID id) {
        Assert.notNull(id, "id is 'null'.");
        try {
            EsDocument document = getEntityDocument();
            DeleteResponse resp = rhlClient.delete(createDeleteRequest(document.index(), document.type(), id));
            return DocWriteResponse.Result.DELETED.equals(resp.getResult());
        } catch (Exception e) {
            throw new EsResponseException(e.getMessage(), e);
        }
    }

    @Override
    public List<ID> delete(List<ID> ids) {
        Assert.notNull(ids, "Cannot delete 'null' as a List.");
        Assert.notEmpty(ids, "Cannot delete empty List.");

        BulkRequest bulk = new BulkRequest()
                .setRefreshPolicy(WriteRequest.RefreshPolicy.NONE)
                .timeout(DEFAULT_TIMEOUT);

        EsDocument document = getEntityDocument();
        String index = document.index(), type = document.type();
        for (ID id : ids){
            bulk.add(createDeleteRequest(index, type, id));
        }

        try {
            BulkResponse resp = rhlClient.bulk(bulk);
            BulkItemResponse[] items = resp.getItems();
            List<ID> failures = Lists.newArrayList();
            for (BulkItemResponse item : items){
                DeleteResponse deleteResponse = (DeleteResponse) item.getResponse();
                if (!DocWriteResponse.Result.DELETED.equals(deleteResponse.getResult())){
                    failures.add((ID) deleteResponse.getId());
                    logger.info("index[{}], type[{}], id[{}]. delete failure message: {}",
                            item.getIndex(), item.getType(), item.getId(), deleteResponse.getResult().name());
                }
            }

            return failures;
        } catch (Exception e) {
            throw new EsResponseException(e.getMessage(), e);
        }
    }

    protected IndexRequest createIndexRequest(T entity){
        EsDocument document = getEntityDocument();
        String index = document.index(), type = document.type();
        Assert.notNull(index, "index is null");
        Assert.notNull(type, "type is null");

        IndexRequest indexRequest = new IndexRequest(index, type)
                .opType(DocWriteRequest.OpType.INDEX)   // TODO DocWriteRequest.OpType.CREATE
                .timeout(getTimeout())
                .source(JSON.toJSONString(entity), XContentType.JSON);  // FIXME 现在只是简单的利用fastjson转换

        if (StringUtils.isNotBlank(entity.getMetaId())){
            indexRequest.id(entity.getMetaId());
        }

        if (StringUtils.isNotBlank(entity.getMetaRouting())){
            indexRequest.routing(entity.getMetaRouting());
        }

        return indexRequest;
    }

    protected DeleteRequest createDeleteRequest(String index, String type, ID id){
        Assert.notNull(index, "index is null");
        Assert.notNull(type, "type is null");
        Assert.notNull(id, "id is null");

        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);

        return deleteRequest;
    }

    private <S extends T> S parse(MultiGetItemResponse item){
        if (item.isFailed()){
            logger.error("multi-get error item >>>> " + item.getFailure().getMessage());
            return null;
        }

        return parse(item.getResponse());
    }

    private <S extends T> S parse(GetResponse resp){
        if (resp.isSourceEmpty()){
            return null;
        }

        S entity = parseRespSource(resp.getSourceAsString());
        entity.setMetaType(resp.getType());
        entity.setMetaIndex(resp.getIndex());
        entity.setMetaId(resp.getId());
        entity.setMetaVersion(resp.getVersion());
        return entity;
    }

    private <S extends T> S parse(BulkItemResponse item, S entity){
        if (item.isFailed()){
            logger.error("bulk error item >>>> " + item.getFailureMessage());
            return entity;
        }

        entity.setMetaType(item.getType());
        entity.setMetaIndex(item.getIndex());
        entity.setMetaId(item.getId());
        entity.setMetaVersion(item.getVersion());
        return entity;
    }

    private <S extends T> S parseRespSource(String source){
        S entity = JSON.parseObject(source, (Type) getEntityClass());
        return entity;
    }

    @Override
    public TimeValue getTimeout(){
        return DEFAULT_TIMEOUT;
    }

    @Override
    public Class<T> getEntityClass(){
        if (entityClass == null) {
            try {
                this.entityClass = resolveReturnedClassFromGenericType();
            } catch (Exception e) {
                throw new ResolveException("Unable to resolve EntityClass. Please use according setter!", e);
            }
        }
        return entityClass;
    }

    private EsDocument getEntityDocument(){
        if (entityDocument == null){
            this.entityDocument = getEntityClass().getAnnotation(EsDocument.class);
        }
        Assert.notNull(entityDocument, "annotation EsDocument.class is null.");
        return entityDocument;
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveReturnedClassFromGenericType() {
        ParameterizedType parameterizedType = resolveReturnedClassFromGenericType(getClass());
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    private ParameterizedType resolveReturnedClassFromGenericType(Class<?> clazz) {
        Object genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type rawtype = parameterizedType.getRawType();
            if (AbstractEsCrudRepository.class.equals(rawtype)) {
                return parameterizedType;
            }
        }
        return resolveReturnedClassFromGenericType(clazz.getSuperclass());
    }
}
