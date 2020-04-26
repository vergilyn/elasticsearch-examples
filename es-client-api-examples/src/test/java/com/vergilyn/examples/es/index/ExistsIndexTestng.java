package com.vergilyn.examples.es.index;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-exists.html">indices-exists.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-indices-exists.html">java-rest-high-indices-exists.html</a>
 */
@Slf4j
public class ExistsIndexTestng extends AbstractEsApiTestng {

    @Test
    public void existsIndexSync(){
        GetIndexRequest request = new GetIndexRequest(ES_INDEX);

        try {
            boolean exists = rhlClient.indices().exists(request, RequestOptions.DEFAULT);

            log.error("exists index[{}] >>>> SUCCESS, is-exists: {}", ES_INDEX, exists);
        } catch (IOException e) {
            log.error("exists index[{}] >>>> FAILURE, cause: {}", ES_INDEX, e.getMessage(), e);
        }
    }
}
