package com.vergilyn.examples.es;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.testng.annotations.Test;

@Slf4j
public class IndexApiTestng extends AbstractEsApiTestng{
    private static final String ID;
    private static final String _SOURCE;

    static {
        ID = "409839163";

        _SOURCE = "{\"properties\": {"
                    + "\"username\": {\"type\": \"keyword\"}, "
                    + "\"content\": {\"type\": \"text\"}, "
                    + "\"update_time\": {\"type\": \"date\", \"format\":\"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||strict_date_optional_time||epoch_millis\"}}}";
    }

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-create-index.html">indices-create-index</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-create-index.html">java-rest-high-create-index</a>
     */
    @Test
    public void createIndex(){
        CreateIndexRequest request = new CreateIndexRequest(ES_INDEX);
        request.mapping(_SOURCE, XContentType.JSON);
        try {
            boolean exists = rhlClient.indices().exists(new GetIndexRequest(ES_INDEX), RequestOptions.DEFAULT);

            if (exists){
                log.warn("create index FAILURE, cause: index[{}] already exists.", ES_INDEX);
                return;
            }

            CreateIndexResponse createIndexResponse = rhlClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("create index[{}] >>>> SUCCESS, response: {}", ES_INDEX, createIndexResponse.isAcknowledged());
        } catch (IOException e) {
            log.error("create index[{}] >>>> FAILURE, cause: {}", ES_INDEX, e.getMessage(), e);
        }
    }

    @Test
    public void indexApiSync() {
        Map<String, Object> map = new HashMap<>();
        map.put("user", "vergilyn");
        map.put("content", "vergilyn elasticsearch client API example.");
        map.put("update_time", LocalDateTime.now().toString());

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
    public void indexApiAsync() {
        // Map
        final Map<String, Object> map = new HashMap<>();
        map.put("user", "vergilyn");
        map.put("content", "vergilyn elasticsearch client API example.");
        map.put("update_time", LocalDateTime.now().toString());

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

    @Test
    public void getById(){
        GetRequest getRequest = new GetRequest(ES_INDEX, ID);
        try {
            GetResponse response = rhlClient.get(getRequest, RequestOptions.DEFAULT);
            log.info("invoke get-by-id >>>> SUCCESS, response: \r\n{}", prettyFormatJson(response));
        } catch (IOException e) {
            log.info("invoke get-by-id >>>> FAILURE, index: {}, id: {}, cause: {}",
                    getRequest.index(), getRequest.id(), e.getMessage(), e);
        }
    }

    @Test
    public void deleteIndex(){
        try {
            AcknowledgedResponse response = rhlClient.indices().delete(new DeleteIndexRequest(ES_INDEX), RequestOptions.DEFAULT);

            log.info("delete index[{}] >>>> SUCCESS, response: {}", ES_INDEX, response.isAcknowledged());
        } catch (IOException e) {
            log.error("delete index[{}] >>>> FAILURE, cause by: {}", ES_INDEX, e.getMessage(), e);
        }
    }
}
