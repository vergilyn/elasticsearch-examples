package com.vergilyn.examples.api;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;


@RunWith(BlockJUnit4ClassRunner.class)
public class InfoApiTest {

    @Test
    public void infoApiTest() {
        try (final RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        )) {
            final MainResponse response = client.info();

            System.out.println(response.getClusterName());
            System.out.println(response.getClusterUuid());
            System.out.println(response.getNodeName());
            System.out.println(response.getVersion());
            System.out.println(response.getBuild());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
