package com.vergilyn.examples.es.client;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.core.MainResponse;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 */
@Slf4j
public class InfoClientTestng extends AbstractEsApiTestng {

    @Test
    public void info() throws IOException {
        MainResponse response = rhlClient.info(RequestOptions.DEFAULT);

        System.out.printf("cluster-name: %s \r\n", response.getClusterName());
        System.out.printf("cluster-uuid: %s \r\n", response.getClusterUuid());
        System.out.printf("node-name: %s \r\n", response.getNodeName());
        System.out.printf("version: %s \r\n", response.getVersion());
        System.out.printf("taglin: %s \r\n", response.getTagline());
    }
}
