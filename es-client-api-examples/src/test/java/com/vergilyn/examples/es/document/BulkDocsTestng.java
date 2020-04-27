package com.vergilyn.examples.es.document;

import java.io.IOException;
import java.time.LocalDateTime;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-bulk.html">docs-bulk.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-document-bulk.html">java-rest-high-document-bulk.html</a>
 */
@Slf4j
public class BulkDocsTestng extends AbstractEsApiTestng {
    protected static final String BULK_ID = "2";

    /**
     * <pre>
     * docs-bulk -> actions: create,index,delete,update
     *   create: re-create-exception "version conflict, document already exists"
     *   index: add or replace a document
     *   update: partial-update, exception "document missing"
     *   delete:
     * </pre>
     */
    @Test
    public void bulkSync() {

        BulkRequest request = new BulkRequest()
                .timeout(TimeValue.timeValueMinutes(2));

        request.add(new IndexRequest(ES_INDEX)
                .id(BULK_ID)
                .source(FIELD_USERNAME, "vergilyn"
                        , FIELD_CONTENT, "create by bulk-api."
                        , FIELD_UPDATE_TIME, LocalDateTime.now().toString())
        );
        request.add(new DeleteRequest(ES_INDEX, ID));

        final BulkResponse bulkResponse;
        try {
            bulkResponse = rhlClient.bulk(request, RequestOptions.DEFAULT);

            bulkResponse.forEach(bulkItemResponse -> {
                /*
                if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX){
                    final IndexResponse indexResponse = (IndexResponse) response;
                } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                    final DeleteResponse deleteResponse = (DeleteResponse) response;
                }
                */

                final DocWriteResponse response = bulkItemResponse.getResponse();
                log.info("invoke bulk-sync >>>> SUCCESS, opType: {}, response: {}", bulkItemResponse.getOpType(), prettyPrintJson(response));
            });
        } catch (IOException e) {
            log.error("invoke bulk-sync >>>> FAILURE, cause: {}", e.getMessage(), e);
        }
    }

    @Test
    public void bulkAsync() {
        final BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest(ES_INDEX, BULK_ID));

        rhlClient.bulkAsync(request, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {
                bulkItemResponses.forEach(bulkItemResponse -> {
                    /*
                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX){
                        final IndexResponse indexResponse = (IndexResponse) response;
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        final DeleteResponse deleteResponse = (DeleteResponse) response;
                    }
                    */

                    final DocWriteResponse response = bulkItemResponse.getResponse();
                    log.info("invoke bulk-async >>>> SUCCESS, opType: {}, response: {}", bulkItemResponse.getOpType(), response.toString());
                });
            }

            @Override
            public void onFailure(Exception e) {
                log.error("invoke bulk-async >>>> FAILURE, cause: {}", e.getMessage(), e);
            }
        });
    }
}
