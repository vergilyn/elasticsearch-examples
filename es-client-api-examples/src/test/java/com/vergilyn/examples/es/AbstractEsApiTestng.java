package com.vergilyn.examples.es;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.collections.Maps;

/**
 * @author vergilyn
 * @date 2020-04-24
 */
@Slf4j
public abstract class AbstractEsApiTestng {
    private final AtomicBoolean IS_INIT = new AtomicBoolean(false);
    protected RestClient restClient;
    protected RestHighLevelClient rhlClient;
    protected static final String ES_INDEX = "vergilyn-es-client-api-examples";
    protected static final String ES_INDEX_TEMPLATE = ES_INDEX + "_template";
    protected static final String ES_INDEX_ALIAS = ES_INDEX + "_alias";
    protected static final String ID = "409839163";

    protected static final String _SOURCE;
    protected static final String FIELD_USERNAME = "username";
    protected static final String FIELD_CONTENT = "content";
    protected static final String FIELD_UPDATE_TIME = "update_time";

    static {
        _SOURCE = "{\"properties\": {"
                + "\"" + FIELD_USERNAME + "\": {\"type\": \"keyword\"}, "
                + "\"" + FIELD_CONTENT + "\": {\"type\": \"text\"}, "
                + "\"" + FIELD_UPDATE_TIME + "\": {\"type\": \"date\", \"format\":\"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||strict_date_optional_time||epoch_millis\"}"
                + "}}";
    }

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

    public String prettyPrintJson(ToXContentObject response){
        return Strings.toString(response, true, true);
    }

    public Map<String, Object> buildData(String username, String content, LocalDateTime dateTime){
        Map<String, Object> data = Maps.newHashMap();

        if (StringUtils.isNotBlank(username)){
            data.put(FIELD_USERNAME, username);
        }

        if (StringUtils.isNotBlank(content)){
            data.put(FIELD_CONTENT, content);
        }

        if (dateTime != null){
            data.put(FIELD_UPDATE_TIME, dateTime.toString());
        }

        return data;
    }
}
