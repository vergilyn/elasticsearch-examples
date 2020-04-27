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

    /**
     * <pre>
     * 1. single-delete >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-delete.html">docs-delete.html</a>
     *   curl -XDELETE "http://127.0.0.1:9200/{index_name}/source_type_all/{_id}"
     *
     * 2. multi-delete >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-delete-by-query.html">docs-delete-by-query.html</a>
     *   curl -XPOST "http://127.0.0.1:9200/{index_name}/_delete_by_query" -H 'Content-Type: application/json' -d'
     *   {
     *      "query": {
     *          "type": {
     *              "value": "source_type_all"
     *          }
     *      }
     *   }'
     * </pre>
     */
    @Test
    public void deleteByIdSync(){
        DeleteRequest deleteRequest = new DeleteRequest(ES_INDEX, ID);
        // deleteRequest.version();
        deleteRequest.timeout(TimeValue.timeValueSeconds(2));
        try {
            DeleteResponse response = rhlClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("invoke delete-by-id >>>> SUCCESS, response: \r\n{}", prettyPrintJson(response));
        } catch (IOException e) {
            log.info("invoke delete-by-id >>>> FAILURE, index: {}, id: {}, cause: {}",
                    deleteRequest.index(), deleteRequest.id(), e.getMessage(), e);
        }
    }
}
