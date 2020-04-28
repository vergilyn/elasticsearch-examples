package com.vergilyn.examples;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author vergilyn
 * @date 2020-04-23
 */
@SpringBootApplication
public class EsAutoconfigureTestApplication implements CommandLineRunner {
    @Autowired
    private RestClient restClient;
    @Autowired
    private RestHighLevelClient rhlClient;

    public static void main(String[] args) {
        SpringApplication.run(EsAutoconfigureTestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.printf("restClient == rhlClient.getLowLevelClient()? %b \r\n", restClient == rhlClient.getLowLevelClient());

        Request request = new Request("GET", "/_cat/indices");

        Response response = restClient.performRequest(request);
        System.out.println("restClient >>>> " + response);

        response = rhlClient.getLowLevelClient().performRequest(request);
        System.out.println("RestHighLevelClient >>>> " + response);

        Runtime.getRuntime().exit(0);
    }
}
