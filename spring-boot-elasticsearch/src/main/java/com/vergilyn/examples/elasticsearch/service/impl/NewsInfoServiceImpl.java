package com.vergilyn.examples.elasticsearch.service.impl;

import java.util.List;

import com.vergilyn.examples.elasticsearch.document.NewsInfoDoc;
import com.vergilyn.examples.elasticsearch.repository.NewsInfoRepository;
import com.vergilyn.examples.elasticsearch.service.NewsInfoService;

import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.AliasBuilder;
import org.springframework.data.elasticsearch.core.query.AliasQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/17
 */
@Service
public class NewsInfoServiceImpl implements NewsInfoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsInfoServiceImpl.class);

    @Autowired
    NewsInfoRepository newsInfoRepository;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    ElasticsearchClient elasticsearchClient;

    @Override
    public void save(NewsInfoDoc info) {
        newsInfoRepository.save(info);
    }



    @Override
    public Boolean addAlias(){
        String index = NewsInfoDoc.class.getAnnotation(Document.class).indexName();
        return addAlias(index + "_alias", NewsInfoDoc.class);
    }

    private <T> Boolean addAlias(String aliasName, Class<T> clazz){
        String indexName = clazz.getAnnotation(Document.class).indexName();
        return addAlias(aliasName, indexName);
    }

    private Boolean addAlias(String aliasName, String indexName){
        String index = NewsInfoDoc.class.getAnnotation(Document.class).indexName();
        List<AliasMetaData> aliasMetaData = elasticsearchTemplate.queryForAlias(index);
        if(aliasMetaData != null ){
            for(AliasMetaData metaData : aliasMetaData){
                if(aliasName.equals(metaData.getAlias())){
                    return false;
                }
            }
        }

        AliasQuery aliasQuery = new AliasBuilder()
                .withIndexName(index)
                .withAliasName(aliasName)
                .build();
//        AliasQuery aliasQuery = new AliasQuery();
//        aliasQuery.setIndexName(index);
//        aliasQuery.setAliasName(aliasName);
//        aliasQuery.setFilterBuilder();
//        aliasQuery.setRouting();
        return elasticsearchTemplate.addAlias(aliasQuery);
    }

    @Override
    public Object search(String queryString, SearchFunction searchFunction) {

        if(SearchFunction.SPRING_ANNOTATION.equals(searchFunction)){
            return springAnnotation(queryString);
        }else if(SearchFunction.SPRING_JAVA.equals(searchFunction)){
           return springJava(queryString);
        }else if(SearchFunction.ELASTIC.equals(searchFunction)){
            return elastic(queryString);
        }
        return null;
    }

    public Object springAnnotation(String queryString) {
        return newsInfoRepository.search(queryString, "3");
    }

    /**
    {
        "_source": {
            "excludes": ["infoContent"]
        },
        "query": {
            "bool": {
                "must": [{
                    "query_string": {
                        "fields": ["listTitle", "infoKeywords", "infoTitle", "infoContent"],
                        "query": "?0",
                        "default_operator": "and"
                    }
                }],
                "must_not": [],
                "should": [],
                "filter": [
                    {"term": {"dataStatus": "?1"}}
                ]
            }
        },
        "from": 0,
        "size": 10,
        "sort": [],
        "aggs": {}
    }
    */
    public List<NewsInfoDoc> elastic(String queryString) {
        //TODO 实现
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
//        sourceBuilder.fields("listTitle", "infoKeywords", "infoTitle", "infoContent");
        // sourceBuilder.fetchSource();

        sourceBuilder.query(
                QueryBuilders.boolQuery()
                        .must(
                                QueryBuilders.queryStringQuery(queryString)
                                        .defaultOperator(Operator.AND)
                                        .field("listTitle").field("infoKeywords").field("infoTitle").field("infoContent"))
                        .filter(QueryBuilders.termQuery("dataStatus", "3"))
        );

        sourceBuilder.from(0);
        sourceBuilder.size(10);
//        sourceBuilder.sort();
//        sourceBuilder.aggregation();
        return null;
    }

/**
 * <pre> multiMatchQuery:
    {
        "bool" : {
            "must" : {
                "multi_match" : {
                    "query" : "积极性奖学金",
                    "fields" : [ "listTitle", "infoKeywords", "infoTitle", "infoContent" ],
                    "operator" : "AND"
                }
            },
            "filter" : {
                "term" : {"dataStatus" : "3"}
            }
        }
    }
 * </pre>
 * <pre> queryStringQuery:
    {
        "bool" : {
            "must" : {
                "query_string" : {
                    "query" : "积极性奖学金",
                    "fields" : [ "listTitle", "infoKeywords", "infoTitle", "infoContent" ],
                    "default_operator" : "and"
                }
            },
            "filter" : {
                "term" : {"dataStatus" : "3"}
            }
        }
    }
 * </pre>
 * <pre> 完整正确json
    {
        "_source": {
            "excludes": ["infoContent"]
        },
        "query": {
            "bool": {
                "must": [{
                    "query_string": {
                        "fields": ["listTitle", "infoKeywords", "infoTitle", "infoContent"],
                        "query": "?0",
                        "default_operator": "and"
                    }
                }],
                "must_not": [],
                "should": [],
                "filter": [
                    {"term": {"dataStatus": "?1"}}
                ]
            }
        },
        "from": 0,
        "size": 10,
        "sort": [],
        "aggs": {}
    }
 * </pre>
 */

    public Object springJava(String queryString) {
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(
//                        QueryBuilders.multiMatchQuery(queryString, "listTitle", "infoKeywords", "infoTitle", "infoContent")
//                                .operator(MatchQueryBuilder.Operator.AND)
                        QueryBuilders.queryStringQuery(queryString)
                                .field("listTitle").field("infoKeywords").field("infoTitle").field("infoContent")
                                .defaultOperator(Operator.AND)
                )
                .filter(QueryBuilders.termQuery("dataStatus", "3"));

        Pageable pageable = new PageRequest(0, 10);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withIndices(ElasticConstant.ElasticIndex.NEWS_INFO.getName())  // 可以是index-alias
//                .withTypes(ElasticConstant.ElasticType.NEWS_INFO.getName())
                .withSourceFilter(new FetchSourceFilter(null,new String[]{"infoContent"}))
                .withQuery(query)
                .withPageable(pageable)
//                .withSort()
//                .addAggregation()
//                .withSearchType()
                .build();

        LOGGER.error("spring-data-elasticsearch java: {}", searchQuery.getQuery().toString());

        Page<NewsInfoDoc> search = newsInfoRepository.search(searchQuery);
        return search != null ? search.getContent() : null;
    }
}
