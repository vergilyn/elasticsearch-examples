package com.vergilyn.examples.es.document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-index_.html">docs-index_.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-document-index.html">java-rest-high-document-index.html</a>
 */
@Slf4j
public class CreateDocsTestng extends AbstractEsApiTestng {

    @Test
    public void createDocsSync() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_USERNAME, "vergilyn");
        map.put(FIELD_CONTENT, "vergilyn elasticsearch client API example.(SYNC)");
        map.put(FIELD_UPDATE_TIME, LocalDateTime.now().toString());

        IndexRequest request = new IndexRequest()
                .index(ES_INDEX)
                .id(ID)
                .source(map);

        try {
            IndexResponse response = rhlClient.index(request, RequestOptions.DEFAULT);
            log.info("invoke index-api-sync >>>> SUCCESS, response: {}", response);
        } catch (IOException e) {
            log.error("invoke index-api-sync >>>> FAILURE, cause: {}", e.getMessage(), e);
        }
    }

    @Test
    public void createDocsAsync() {
        final Map<String, Object> map = new HashMap<>();
        map.put(FIELD_USERNAME, "vergilyn");
        map.put(FIELD_CONTENT, "vergilyn elasticsearch client API example.(ASYNC)");
        map.put(FIELD_UPDATE_TIME, LocalDateTime.now().toString());

        IndexRequest request = new IndexRequest()
                .index(ES_INDEX)
                .id(ID)
                .source(map);

        rhlClient.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(final IndexResponse response) {
                log.info("invoke index-api-async >>>> SUCCESS, response: {}", response);
            }

            @Override
            public void onFailure(final Exception e) {
                log.error("invoke index-api-async >>>> FAILURE, cause: {}", e.getMessage(), e);
            }
        });

        sleepSeconds(10);
    }
}
