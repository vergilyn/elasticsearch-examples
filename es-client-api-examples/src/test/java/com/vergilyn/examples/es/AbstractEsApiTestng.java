package com.vergilyn.examples.es;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import static com.alibaba.fastjson.serializer.SerializerFeature.MapSortField;
import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.alibaba.fastjson.serializer.SerializerFeature.SortField;

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
    protected static final String ES_INDEX_ALIAS = ES_INDEX + "_alias";

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

    public void preventExit(){
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    public void sleepSeconds(long timeout){
        sleep(TimeUnit.SECONDS, timeout);
    }

    public void sleep(TimeUnit unit, long timeout){
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    public String prettyFormatJson(Object object){
        boolean isCollection = false;
        if (object instanceof Collection){
            isCollection = true;
        }

        return prettyFormatJson(object.toString(), isCollection);
    }

    public String prettyFormatJson(String json, boolean isCollection){
        SerializerFeature[] serializerFeature = {PrettyFormat, SortField, MapSortField};

        if (isCollection){
            return JSON.toJSONString(JSON.parseArray(json), serializerFeature);
        }

        return JSON.toJSONString(JSON.parseObject(json), serializerFeature);
    }
}
