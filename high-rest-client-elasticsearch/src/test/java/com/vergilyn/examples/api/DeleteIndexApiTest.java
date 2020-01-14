package com.vergilyn.examples.api;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class DeleteIndexApiTest {

    @Test
    public void deleteIndexApiSyncTest() {
        try (final RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        )) {
            final DeleteIndexRequest request = new DeleteIndexRequest()
                    .indices("index")
                    .timeout(TimeValue.timeValueMinutes(2));

            final DeleteIndexResponse response = client.indices().delete(request);
            assertThat(response.isAcknowledged(), is(true));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteIndexApiAsyncTest() {
        try (final RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        )) {
            final DeleteIndexRequest request = new DeleteIndexRequest()
                    .indices("index")
                    .timeout(TimeValue.timeValueMinutes(2));

            client.indices().deleteAsync(request, new ActionListener<DeleteIndexResponse>() {
                @Override
                public void onResponse(final DeleteIndexResponse response) {
                    assertThat(response.isAcknowledged(), is(true));
                }

                @Override
                public void onFailure(final Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
