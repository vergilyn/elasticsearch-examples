package com.vergilyn.examples.es;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class IndexApiTestng extends AbstractEsApiTestng{

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-create-index.html">indices-create-index</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-create-index.html">java-rest-high-create-index</a>
     */
    @Test
    public void createIndex(){
        CreateIndexRequest request = new CreateIndexRequest(ES_INDEX);
        request.mapping("{\"properties\": {\"field_text\": { \"type\": \"text\"}}}", XContentType.JSON);
        try {
            boolean exists = rhlClient.indices().exists(new GetIndexRequest(ES_INDEX), RequestOptions.DEFAULT);

            if (exists){
                log.warn("create index failure, cause: index[{}] already exist.", ES_INDEX);
                return;
            }

            CreateIndexResponse createIndexResponse = rhlClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("create index[{}] response: {}", ES_INDEX, createIndexResponse.isAcknowledged());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void indexApiSyncTest() throws IOException {
        // Map
        final Map<String, Object> map = new HashMap<>();
        map.put("user", "vergilyn");
        map.put("message", "elasticsearch-rest-high-level-client-sample");

        // XContentBuilder
        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "vergilyn");
            builder.field("message", "elasticsearch-rest-high-level-client-sample");
        }
        builder.endObject();

        final IndexRequest request = new IndexRequest()
                .index("index")
                .type("logs")
                .id("id")
                .timeout(TimeValue.timeValueMinutes(2))
                // Map
                .source(map);
                // XContentBUilder
                // .source(builder);
                // Object key-pairs
                // .source("user", "vergilyn",
                //         "message", "elasticsearch-rest-high-level-client-sample")

        final IndexResponse response = rhlClient.index(request, RequestOptions.DEFAULT);

        assertThat(response.getIndex(), is("index"));
        assertThat(response.getType(), is("logs"));
        assertThat(response.getId(), is("id"));

        if (response.getResult() == DocWriteResponse.Result.CREATED) {
            assertThat(response.getVersion(), is(1L));
        } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
            assertThat(response.getVersion(), is(greaterThan(1L)));
        }
    }

    @Test
    public void indexApiAsyncTest() {
            // Map
        final Map<String, Object> map = new HashMap<>();
        map.put("user", "vergilyn");
        map.put("message", "elasticsearch-rest-high-level-client-sample");

        final IndexRequest request = new IndexRequest()
                .index("index")
                .type("logs")
                .id("id")
                .timeout(TimeValue.timeValueMinutes(2))
                .source(map);

        rhlClient.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(final IndexResponse response) {
                assertThat(response.getIndex(), is("index"));
                assertThat(response.getType(), is("logs"));
                assertThat(response.getId(), is("id"));

                if (response.getResult() == DocWriteResponse.Result.CREATED) {
                    assertThat(response.getVersion(), is(1L));
                } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                    assertThat(response.getVersion(), is(greaterThan(1L)));
                }
            }

            @Override
            public void onFailure(final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
