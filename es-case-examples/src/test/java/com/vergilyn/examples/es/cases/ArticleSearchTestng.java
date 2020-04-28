package com.vergilyn.examples.es.cases;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.vergilyn.examples.es.AbstractEsClientTestng;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-28
 */
@Slf4j
public class ArticleSearchTestng extends AbstractEsClientTestng {
    private static final String ES_INDEX_SOURCE_RESOURCE_PATH = "article-index-source.json";
    private static final String ES_INDEX = "vergilyn-article";
    private static final String ES_INDEX_ALIAS = ES_INDEX + "_alias";

    @Test
    public void createIndex() throws IOException {
        String settings = getIndexSettings();

        CreateIndexRequest request = new CreateIndexRequest(ES_INDEX);
        request.source(settings, XContentType.JSON);
        request.alias(new Alias(ES_INDEX_ALIAS));

        CreateIndexResponse createIndexResponse = rhlClient.indices().create(request, RequestOptions.DEFAULT);
        log.info("create index[{}] >>>> SUCCESS, response: {}", ES_INDEX, createIndexResponse.isAcknowledged());
    }

    @Test
    public void deleteIndex() throws IOException {
        AcknowledgedResponse response = rhlClient.indices()
                            .delete(new DeleteIndexRequest(ES_INDEX), RequestOptions.DEFAULT);

        log.info("delete index[{}] >>>> SUCCESS, response: {}", ES_INDEX, prettyPrintJson(response));
    }

    public String getIndexSettings() throws IOException {
        String settings = null;
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL input =  classLoader.getResource(ES_INDEX_SOURCE_RESOURCE_PATH);

        settings = IOUtils.toString(input, StandardCharsets.UTF_8.name());

        return settings;
    }
}
