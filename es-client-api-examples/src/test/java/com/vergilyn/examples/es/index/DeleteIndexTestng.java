package com.vergilyn.examples.es.index;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-delete-index.html">indices-delete-index.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-delete-index.html">java-rest-high-delete-index.html</a>
 */
@Slf4j
public class DeleteIndexTestng extends AbstractEsApiTestng {

    @Test
    public void deleteIndexSync(){
        try {
            AcknowledgedResponse response = rhlClient.indices()
                    .delete(new DeleteIndexRequest(ES_INDEX), RequestOptions.DEFAULT);

            log.info("delete index[{}] >>>> SUCCESS, response: {}", ES_INDEX, response.isAcknowledged());
        } catch (IOException e) {
            log.error("delete index[{}] >>>> FAILURE, cause by: {}", ES_INDEX, e.getMessage(), e);
        }
    }

}
