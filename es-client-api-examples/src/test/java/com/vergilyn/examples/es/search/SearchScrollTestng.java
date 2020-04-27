package com.vergilyn.examples.es.search;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-27
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/search-request-body.html#request-body-search-scroll">search-request-body.html#request-body-search-scroll</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-search-scroll.html">java-rest-high-search-scroll.html</a>
 */
@Slf4j
public class SearchScrollTestng extends AbstractEsApiTestng {

    @Test
    public void scrollSync() throws IOException {
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));

        SearchRequest searchRequest = new SearchRequest(ES_INDEX_ALIAS)
                                        .scroll(scroll);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.termsQuery(FIELD_USERNAME, "vergilyn"))
                    .from(0)
                    .size(1);

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);

        String scrollId = response.getScrollId();
        SearchHit[] searchHits = response.getHits().getHits();

        log.info("search-scroll >>>> SUCCESS, response: scrollId[{}], hits[{}], {}",
                    scrollId, response.getHits().getTotalHits().value, prettyPrintJson(response));


        while (searchHits != null && searchHits.length > 0) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);

            response = rhlClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = response.getScrollId();
            searchHits = response.getHits().getHits();
            log.info("search-scroll >>>> SUCCESS, response: scrollId[{}], hits[{}], {}",
                    scrollId, response.getHits().getTotalHits().value, prettyPrintJson(response));
        }

        // clear scroll
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = rhlClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        log.info("search-scroll clear >>>> SUCCESS, response: success[{}], scrollId[{}], hits[{}], {}",
                clearScrollResponse.isSucceeded(),
                scrollId, response.getHits().getTotalHits().value, prettyPrintJson(response));
    }
}
