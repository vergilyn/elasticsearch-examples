package com.vergilyn.examples.es;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

/**
 * @author vergilyn
 * @date 2020-04-24
 */
@Slf4j
public abstract class AbstractEsApiTestng {
    private AtomicBoolean IS_INIT = new AtomicBoolean(false);
    protected RestClient restClient;
    protected RestHighLevelClient rhlClient;
    protected static final String ES_INDEX = "vergilyn-es-client-api-examples";
    protected static final String ES_INDEX_ALIAS = "vergilyn-es-client-api-examples_alias";

    @BeforeTest
    public void init(){
        if (!IS_INIT.compareAndSet(false, true)){
            return;
        }

        RestClientBuilder builder = RestClient.builder(new HttpHost("127.0.0.1", 9200));

        builder.setRequestConfigCallback(requestConfigCallback -> {
            requestConfigCallback.setConnectionRequestTimeout(1_000);
            requestConfigCallback.setConnectTimeout(1_000);
            requestConfigCallback.setSocketTimeout(30_000);
            requestConfigCallback.setMaxRedirects(50);
            return requestConfigCallback;
        });

        builder.setHttpClientConfigCallback(httpClientConfigCallback -> {
            httpClientConfigCallback.setMaxConnPerRoute(10);
            httpClientConfigCallback.setMaxConnTotal(30);
            return httpClientConfigCallback;
        });

        rhlClient = new RestHighLevelClient(builder);
        restClient = rhlClient.getLowLevelClient();
    }

    @AfterTest
    public void destroy(){
        try {
            rhlClient.close();
            restClient.close();
        } catch (IOException e) {
            log.error("destroy elasticsearch client failure", e);
        }
    }
}
