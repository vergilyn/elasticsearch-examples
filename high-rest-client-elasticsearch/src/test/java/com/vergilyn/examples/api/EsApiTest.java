package com.vergilyn.examples.api;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.ByteBufferReference;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 貌似：<br/>
 *   1. rhlClient只提供了部分操作document的client，不能直接create/delete index，但可以通过{@link RestHighLevelClient#indices()} create/delete；<br/>
 *   2. 暂时想到可以创建index-template的方法，通过{@link RestClient#performRequest(String, String, Map, HttpEntity, Header...)}
 *   3. 例如，update_by_query、delete_by_query没有被rhlClient提供。并且貌似无法通过{@link RestHighLevelClient}或{@link RestClient}得到{@link ElasticsearchClient}
 * <p>
 *     {@link RestClient}最大问题，response结果转换。可以参考{@link RestHighLevelClient}的源代码参考。
 * </p>
 */
public class EsApiTest {
    private static final Logger logger = LoggerFactory.getLogger(EsApiTest.class);

    protected static final String _INDEX_TEMPLATE = "test_app_data_x_template";
    protected static final String _TEMPLATE_CLASSPATH_RESOURCE = "elastic-template/app_data_x_template.json";
    protected static final Long APP_ID = 1L;
    protected static final String _INDEX = "app_data_" + APP_ID;
    protected static final String _INDEX_ALIAS = _INDEX + "_alias";
    protected static final String _TYPE = "source_type_all";
    protected static final String UTF_8 = "utf-8";
    @Autowired
    private RestHighLevelClient rhlClient;

    /** 1. 删除索引模版：app_id_template >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-templates.html#delete">indices-templates.html#delete</a>
     <pre>
        curl -XDELETE "http://127.0.0.1:9200/_template/app_id_template"
     </pre>
     */
    @Test
    public void deleteTemplate(){
        System.out.println("\r\n===================== deleteTemplate() =======================");

        try {
            Response resp = rhlClient.getLowLevelClient().performRequest("DELETE", "_template/" + _INDEX_TEMPLATE, Collections.emptyMap());
            System.out.println(responseJson(resp));
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }

    }

    /** 2. 创建索引模版：app_id_template >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-templates.html">indices-templates.html</a>
     <pre>
        curl -XPUT "http://127.0.0.1:9200/_template/app_id_template" -H 'Content-Type: application/json' -d'
        {...}
     </pre>
     */
    @Test
    public void createIndexTemplate(){
        System.out.println("\r\n===================== createTemplateIndex() =======================");

        Resource resource = new ClassPathResource(_TEMPLATE_CLASSPATH_RESOURCE);
        // ResourceUtils.getFile("classpath:elastic/es_test_template.json");

        // V-NOTE: rhlClient未提供创建index-template的API；
        try {
            String jsonString = FileUtils.readFileToString(resource.getFile(), UTF_8);
            HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
            Response resp = rhlClient.getLowLevelClient().performRequest("PUT", "_template/" + _INDEX_TEMPLATE, Collections.emptyMap(), entity);
            System.out.println(responseJson(resp));
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }
    }

    /** 3. 删除索引：app_id_1 >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-delete-index.html">indices-delete-index.html</a>,
     <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.3/java-rest-high-delete-index.html">java-rest-high-delete-index.html</a>
     <pre>
        curl -XDELETE "http://127.0.0.1:9200/app_id_1"
     </pre>
     */
    @Test
    public void deleteIndex(){
        System.out.println("\r\n===================== deleteIndex() =======================");

        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(_INDEX);
            // v-note: rhlClient提供了IndicesClient来操作indices
            DeleteIndexResponse deleteResponse = rhlClient.indices().delete(deleteIndexRequest);

            System.out.println(parse(deleteResponse.toXContent(JsonXContent.contentBuilder(), null)));
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }
    }

    /** 4. 创建索引：app_id_1 >>>> <a https://www.elastic.co/guide/en/elasticsearch/reference/6.3/indices-create-index.html>indices-create-index</a>,
     <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.3/java-rest-high-create-index.html">java-rest-high-create-index.html</a>
     <pre>
        curl -XPUT "http://127.0.0.1:9200/app_id_1"
     </pre>
     */
    @Test
    public void createIndex(){
        System.out.println("\r\n===================== createIndex() =======================");

        CreateIndexRequest createIndexRequest = new CreateIndexRequest(_INDEX);
        try {
            CreateIndexResponse response = rhlClient.indices().create(createIndexRequest);
            System.out.println(parse(response.toXContent(JsonXContent.contentBuilder(), null)));
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }
    }

    /** 5. 利用`_bulk`批量创建索引app_id_1的数据 >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-bulk.html">docs-bulk.html</a>
     * <pre>
     * docs-bulk -> actions: create,index,delete,update
     *   create: re-create-exception "version conflict, document already exists"
     *   index: add or replace a document
     *   update: partial-update, exception "document missing"
     *   delete:
     * </pre>
     * 备注: request-body json不能是格式化后的格式
     */
    @Test
    public void createIndexData(){
        System.out.println("\r\n===================== createIndexData() =======================");
        try {
            // 演示：若将bulk定义在文件中 XXX 感觉转换太麻烦，待商榷
            Resource resource = new ClassPathResource("elastic-template/es_bulk_test_template_data.txt");
            byte[] bulkJson = FileUtils.readFileToByteArray(resource.getFile());
            ByteBuffer byteBuffer = ByteBuffer.wrap(bulkJson);
            BytesReference bytesReference = new ByteBufferReference(byteBuffer);

            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.add(bytesReference, _INDEX, _TYPE, false, XContentType.JSON);

            BulkResponse bulkResponse = rhlClient.bulk(bulkRequest);
            for (BulkItemResponse response : bulkResponse.getItems()) {
                System.out.print(response.getId() + ": ");
                if (response.isFailed()){
                    System.out.println(response.getFailureMessage());
                }else {
                    // ES-6.3.0没有string()方法
                    // System.out.println(response.toXContent(JsonXContent.contentBuilder(), null).string());
                    System.out.println(parse(response.toXContent(JsonXContent.contentBuilder(), null)));
                }
                System.out.print("\r\n");
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }
    }

    /** 6. 获取document
     <pre>
     1. _mget >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-multi-get.html">docs-multi-get.html</a>
        curl -XGET "http://127.0.0.1:9200/app_id_1_alias/source_type_all/_mget" -H 'Content-Type: application/json' -d'
        {
            "ids":["1", "2", "3", "4"]
        }'

     2. _search >>>> <a href="">https://www.elastic.co/guide/en/elasticsearch/reference/6.3/query-dsl-ids-query.html</a>
        curl -XGET "http://127.0.0.1:9200/app_id_1_alias/source_type_all/_search" -H 'Content-Type: application/json' -d'
        {
            "query": {
                "ids": {
                    "type": "source_type_all",
                    "values": ["1", "2", "3", "4"]
                }
            }
        }'

     3. _search >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/query-dsl-type-query.html">query-dsl-type-query.html</a>
        curl -XGET "http://127.0.0.1:9200/app_id_1_alias/_search" -H 'Content-Type: application/json' -d'
        {
            "query": {
                "type": {
                    "value": "source_type_all"
                }
            }
        }'
     */
    @Test
    public void getIndexData(){
        System.out.println("\r\n===================== getIndexData() =======================");

        // query-dsl-type-query
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(QueryBuilders.typeQuery(_TYPE))
                .from(0)
                .size(10)
                .timeout(TimeValue.timeValueMinutes(2));

        SearchRequest request = new SearchRequest().indices(_INDEX_ALIAS).types(_TYPE)
                .source(builder);
        try {
            // v-think：如何转换resp为javabean？(现在想到: 写个公共方法处理SearchHit)
            SearchResponse response = rhlClient.search(request);

            System.out.println("=== HTTP Request ===");
            System.out.println("status: " + response.status());
            System.out.println("took: " + response.getTook());
            System.out.println("timed_out: " + response.isTimedOut());

            System.out.println("\n=== Hits ===");
            SearchHits hits = response.getHits();
            System.out.println("total_hits: " + hits.getTotalHits());

            for (SearchHit hit : hits.getHits()) {
                System.out.println("\n=== Documents ===");
                System.out.println("id: " + hit.getId());
                System.out.println("source: " + hit.getSourceAsString());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }


    }

    /** 7. 删除document
     <pre>
     7.1 single-delete >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-delete.html">docs-delete.html</a>
        curl -XDELETE "http://127.0.0.1:9200/app_id_1/source_type_all/{_id}"

     7.2 multi-delete >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-delete-by-query.html">docs-delete-by-query.html</a>
        curl -XPOST "http://127.0.0.1:9200/app_id_1/_delete_by_query" -H 'Content-Type: application/json' -d'
        {
            "query": {
                "type": {
                    "value": "source_type_all"
                }
            }
        }'
     </pre>
     */
    @Test
    public void deleteDocument(){
        System.out.println("\r\n===================== deleteDocument() =======================");

        // v-think: 虽然能实现delete_by_query，但感觉不够理想；
        // 与其用builder构建，不如直接写json
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.typeQuery(_TYPE));
        /*
            POST app_id_1/_delete_by_query
            {
              "query": {
                "type": {
                  "value": "source_type_all"
                }
              }
            }
         */

        HttpEntity entity = new NStringEntity(sourceBuilder.toString(), ContentType.APPLICATION_JSON);

        try {
            // v-note：可以参考HighLevelRestClient中是如何处理resp的。但感觉都不够好
            Response resp = rhlClient.getLowLevelClient().performRequest("POST", _INDEX_ALIAS + "/_delete_by_query", Collections.emptyMap(), entity);
            System.out.println(IOUtils.toString(resp.getEntity().getContent(), UTF_8));
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }
    }

    /** 8. 更新document(重索引document)，只要document被更新，就会reindex。
     <pre>
     8.1 updates with a partial document >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-update.html#_updates_with_a_partial_document">docs-update.html#_updates_with_a_partial_document</a>
        curl -XPOST "http://127.0.0.1:9200/app_id_1/source_type_all/2/_update" -H 'Content-Type: application/json' -d'
        {
            "doc":{
                "online_time": "2018-06-18 23:00:00"
            }
        }'
     备注：若update未改变任何field，那么默认情况下会ignore此次request，返回`result: noop`；（是否意味着并不会reindex-document？）可以通过`detect_noop: false`来禁用此功能。
     (详见：<a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-update.html#_detecting_noop_updates">docs-update.html#_detecting_noop_updates</a>)

     8.2 op_type: create(default)、index，create: re-create-exception；index: add or replace a document。
        curl -XPUT "http://127.0.0.1:9200/app_id_1/source_type_all/4?op_type=index" -H 'Content-Type: application/json' -d'
        {
            "online_time": "2018-06-14 19:00:00"
        }'

     8.3 <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-update-by-query.html">_update_by_query</a> will incr _version(所以可以用来reindex-document)
        curl -XPOST "http://127.0.0.1:9200/app_id_1/source_type_all/_update_by_query?conflicts=proceed" -H 'Content-Type: application/json' -d'
        {
            "query":{
                "type":{
                    "value":"source_type_all"
                }
            }
        }'
     备注：如何批量更新某个field，不能`doc`，但可以`script`？

     8.4 _bulk
     </pre>

     * v-note：如果只是为了reindex-document，完全可以只用`_update_by_query`; 若要批量更新，(暂时)觉得`_bulk`是最高效的。
     */
    @Test
    public void updateDocument(){
        System.out.println("\r\n===================== updateDocument() =======================");

        // 8.3 reindex-document v-think： high-level-rest-client也未提供直接操作`_update_by_query`的API，
        String endpoint = String.format("%s/%s/_update_by_query", _INDEX, _TYPE);
        endpoint = String.format("%s/_update_by_query", "app_id_*");
        try {
            Response resp = rhlClient.getLowLevelClient().performRequest("POST", endpoint, Collections.emptyMap());
            System.out.println(RestStatus.OK.getStatus() == resp.getStatusLine().getStatusCode());
            System.out.println(IOUtils.toString(resp.getEntity().getContent(), UTF_8));
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }
    }

    /** 9. 搜索document >>>>
     <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/search.html">search.html</a>、
     <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/query-dsl.html">query-dsl.html</a>
     <pre>
     9.1 multi_match：best_field、most_type
     best_field：默认情况下best_field#_score = field.max_score，若指定tie_breaker：
        _score = field.max_score + other_fields.sum_score * tie_breaker

     most_fields：优先返回匹配了更多字段的document。

     curl -XGET "http://127.0.0.1:9200/app_id_1/source_type_all/_search" -H 'Content-Type: application/json' -d'
        {
            "query": {
                "bool": {
                    "must": [{
                            "multi_match": {
                            "query": "原创文章内容",
                            "type": "best_fields",
                            "fields": ["content^0.2", "synopsis^0.6", "info_title^0.8", "list_title^0.9", "keywords^1.0"],
                            "operator": "or",
                            "tie_breaker": 0.4
                            }
                    }],
                    "filter": [
                        {"term": {"source_type": 1}},
                        {"range": {
                            "online_time": {"gte": "2018-06-14 12:00:00"}
                        }}
                    ]
                }
            }
     }'
     备注：filter不会计算_score

     </pre>
     */
    @Test
    public void searchDocument(){
        String text = "原创文章内容";
        int[] sourceTypes = {1, 2};
        String onlineTime = "2018-06-14 12:00:00";

        SearchSourceBuilder builder = new SearchSourceBuilder().from(0).size(10)
                                    .timeout(TimeValue.timeValueMinutes(2));

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 查询时指定boost
        MultiMatchQueryBuilder matchBuilder = QueryBuilders.multiMatchQuery(text)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                .field("content", 0.2F)
                .field("synopsis", 0.6F)
                .field("info_title", 0.8F)
                .field("list_title", 0.9F)
                .field("keywords", 1.0F)
                .operator(Operator.OR)
                .tieBreaker(0.4F);
        boolQuery.must(matchBuilder);

        boolQuery.filter(QueryBuilders.termsQuery("source_type", sourceTypes))
                .filter(QueryBuilders.rangeQuery("online_time").gte(onlineTime));

        builder.query(boolQuery);

        builder.fetchSource(null, new String[]{"content", "synopsis"});

        // v-note：搜索时最好用index-alias
        SearchRequest request = new SearchRequest().indices(_INDEX_ALIAS).types(_TYPE)
                .source(builder);
        System.out.println("\n=== request ===");
        System.out.println(builder.toString());

        SearchResponse response;
        try {
            response = rhlClient.search(request);

            final SearchHits hits = response.getHits();
            for (SearchHit hit : hits.getHits()) {
                System.out.println("\n=== Documents ===");
                System.out.println("score: " + hit.getScore());
                System.out.println("source: " + hit.getSourceAsString());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }
    }

    private String parse(XContentBuilder builder){
        BytesReference bytes = BytesReference.bytes(builder);
        return bytes.utf8ToString();
    }

    public String responseJson(Response resp){
        try {
            return IOUtils.toString(resp.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
