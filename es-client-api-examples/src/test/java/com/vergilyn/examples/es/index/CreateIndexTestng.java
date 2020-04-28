package com.vergilyn.examples.es.index;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.testng.annotations.Test;

/**
 *
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-create-index.html">indices-create-index.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-create-index.html">java-rest-high-create-index.html</a>
 */
@Slf4j
public class CreateIndexTestng extends AbstractEsApiTestng {

    @Test
    public void createIndexSync(){
        CreateIndexRequest request = new CreateIndexRequest(ES_INDEX);
        // request.settings(Source, XContentType);
        request.mapping(_SOURCE, XContentType.JSON);
        // request.alias(new Alias(ES_INDEX_ALIAS));

        try {
            CreateIndexResponse createIndexResponse = rhlClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("create index[{}] >>>> SUCCESS, response: {}", ES_INDEX, createIndexResponse.isAcknowledged());
        } catch (IOException e) {
            log.error("create index[{}] >>>> FAILURE, cause: {}", ES_INDEX, e.getMessage(), e);
        }
    }
}
