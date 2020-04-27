package com.vergilyn.examples.es.search;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-27
 */
@Slf4j
public class SearchTestng extends AbstractEsApiTestng {

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/search-search.html">search-search.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-search.html">java-rest-high-search.html</a>
     */
    @Test
    public void searchApiSync() throws IOException {
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .indexBoost(ES_INDEX_ALIAS, 1)
                .query(QueryBuilders.matchQuery(FIELD_USERNAME, "vergilyn"))
                .from(0)
                .size(5)
                .timeout(TimeValue.timeValueMinutes(2));

        SearchRequest request = new SearchRequest().source(builder);

        SearchResponse response = rhlClient.search(request, RequestOptions.DEFAULT);

        log.info("search-api >>>> SUCCESS, response: {}", prettyPrintJson(response));
    }
}
