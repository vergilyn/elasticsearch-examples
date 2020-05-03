package com.vergilyn.examples.es.data;

import com.vergilyn.examples.es.data.document.ArticleDocument;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * <a href="https://docs.spring.io/spring-data/elasticsearch/docs/3.2.7.RELEASE/reference/html/#elasticsearch.operations.resttemplate">ElasticsearchRestTemplate</a>
 * 底层基于<a href="https://docs.spring.io/spring-data/elasticsearch/docs/3.2.7.RELEASE/reference/html/#elasticsearch.clients.rest">High Level REST Client.</a>
 * @author vergilyn
 * @date 2020-05-03
 */
@Slf4j
public class EsRestTemplateTest extends AbstractEsSpringBootTest {
    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Test
    public void test(){
        boolean indexExists = restTemplate.indexExists(ArticleDocument.class);

        Assert.assertTrue(indexExists);
    }

    @Test
    public void rhlClient(){
        RestHighLevelClient rhlClient = restTemplate.getClient();
    }

    /**
     * TODO 2020-05-03
     */
    @Test
    public void queryForList(){
        // restTemplate.queryForList();
    }
}
