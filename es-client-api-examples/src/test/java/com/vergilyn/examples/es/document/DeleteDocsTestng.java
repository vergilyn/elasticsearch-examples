package com.vergilyn.examples.es.document;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-delete.html">docs-delete.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-document-delete.html">java-rest-high-document-delete.html</a>
 */
@Slf4j
public class DeleteDocsTestng extends AbstractEsApiTestng {

    @Test
    public void deleteByIdSync(){
        DeleteRequest deleteRequest = new DeleteRequest(ES_INDEX, ID);
        // deleteRequest.version();
        deleteRequest.timeout(TimeValue.timeValueSeconds(2));
        try {
            DeleteResponse response = rhlClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("invoke delete-by-id >>>> SUCCESS, response: \r\n{}", prettyFormatJson(response));
        } catch (IOException e) {
            log.info("invoke delete-by-id >>>> FAILURE, index: {}, id: {}, cause: {}",
                    deleteRequest.index(), deleteRequest.id(), e.getMessage(), e);
        }
    }
}
