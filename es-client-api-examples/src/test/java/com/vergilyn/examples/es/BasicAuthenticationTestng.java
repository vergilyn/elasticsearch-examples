package com.vergilyn.examples.es;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class BasicAuthenticationTestng {

    @Test
    public void basicAuthenticationTest() {
        try (final RestHighLevelClient client = new RestHighLevelClient(RestClient
                .builder(new HttpHost("localhost", 9200, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    final BasicCredentialsProvider provider = new BasicCredentialsProvider();
                    provider.setCredentials(
                            AuthScope.ANY,
                            new UsernamePasswordCredentials(
                                    "username",
                                    "password"
                            )
                    );

                    return httpClientBuilder.setDefaultCredentialsProvider(provider);
                })
        )) {
            // Do something.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
