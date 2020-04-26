package com.vergilyn.examples.es.document;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-get.html">docs-get.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-document-get.html">java-rest-high-document-get.html</a>
 */
@Slf4j
public class GetDocsTestng extends AbstractEsApiTestng {

    @Test
    public void getByIdSync(){
        GetRequest request = new GetRequest(ES_INDEX, ID);
        try {
            GetResponse response = rhlClient.get(request, RequestOptions.DEFAULT);
            // String source = response.getSourceAsString();
            log.info("invoke get-by-id >>>> SUCCESS, response: \r\n{}", prettyFormatJson(response));
        } catch (IOException e) {
            log.info("invoke get-by-id >>>> FAILURE, index: {}, id: {}, cause: {}",
                    request.index(), request.id(), e.getMessage(), e);
        }
    }
}
