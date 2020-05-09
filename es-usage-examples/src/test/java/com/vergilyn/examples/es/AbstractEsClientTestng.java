package com.vergilyn.examples.es;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

/**
 * @author vergilyn
 * @date 2020-04-24
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-low-usage-initialization.html">java-rest-low-usage-initialization.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-getting-started-initialization.html">java-rest-high-getting-started-initialization.html</a>
 */
@Slf4j
public abstract class AbstractEsClientTestng {
    private final AtomicBoolean IS_INIT = new AtomicBoolean(false);
    protected RestClient restClient;
    protected RestHighLevelClient rhlClient;
    protected ObjectMapper objectMapper;

    @BeforeTest
    public void beforeTest(){
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

        objectMapper = new ObjectMapper();
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

    protected String getResource(String resourcePath) throws IOException {
        ClassLoader classLoader = AbstractEsClientTestng.class.getClassLoader();
        URL input = classLoader.getResource(resourcePath);

        if (input == null){
            throw new FileNotFoundException();
        }

        return IOUtils.toString(input, StandardCharsets.UTF_8.name());
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
}
