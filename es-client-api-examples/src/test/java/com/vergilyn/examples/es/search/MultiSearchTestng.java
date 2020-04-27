package com.vergilyn.examples.es.search;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-27
 */
public class MultiSearchTestng extends AbstractEsApiTestng {

    /**
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/query-dsl-multi-match-query.html">query-dsl-multi-match-query.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-multi-search.html">java-rest-high-multi-search.html</a>
     */
    @Test
    public void sync(){

    }

    /** 9. 搜索document >>>>
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/search.html">search.html</a>、
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/query-dsl.html">query-dsl.html</a>
     * <pre>
     * 1. multi_match：best_field、most_type
     *   best_field：默认情况下best_field#_score = field.max_score，若指定tie_breaker：
     *   _score = field.max_score + other_fields.sum_score * tie_breaker
     *
     * most_fields：优先返回匹配了更多字段的document。
     *
     * curl -XGET "http://127.0.0.1:9200/app_id_1/source_type_all/_search" -H 'Content-Type: application/json' -d'
     * {
     *   "query": {
     *     "bool": {
     *       "must": [{
     *         "multi_match": {
     *           "query": "原创文章内容",
     *           "type": "best_fields",
     *           "fields": ["content^0.2", "synopsis^0.6", "info_title^0.8", "list_title^0.9", "keywords^1.0"],
     *           "operator": "or",
     *           "tie_breaker": 0.4
     *         }
     *       }],
     *       "filter": [
     *         {"term": {"source_type": 1}},
     *         {"range": {
     *            "online_time": {"gte": "2018-06-14 12:00:00"}
     *         }}
     *       ]
     *     }
     *   }
     * }'
     * 备注：filter不会计算_score
     *
     * </pre>
     */
    public void backup(){
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
        SearchRequest request = new SearchRequest().indices("index-alias")
                .source(builder);
        System.out.println("\n=== request ===");
        System.out.println(builder.toString());

        SearchResponse response;
        try {
            response = rhlClient.search(request, RequestOptions.DEFAULT);

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
}
