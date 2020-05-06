package com.vergilyn.examples.es.data;

import com.vergilyn.examples.es.data.document.ArticleDocument;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * <a href="https://docs.spring.io/spring-data/elasticsearch/docs/3.2.7.RELEASE/reference/html/#elasticsearch.operations.template">ElasticsearchTemplate</a>
 * The `ElasticsearchTemplate` is an implementation of the `ElasticsearchOperations` interface using the `Transport Client`.
 * (The well known ·TransportClient· is deprecated as of Elasticsearch 7 and will be removed in Elasticsearch 8)
 * @author vergilyn
 * @date 2020-05-03
 */
@SpringBootTest(classes = SpringDataEsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = "spring.profiles.active=es")
@Slf4j
public class EsTemplateTest {
    /**
     * FIXME 2020-05-03 autowired failure! (`RestTemplate` success!)
     */
    @Autowired
    private ElasticsearchTemplate template;

    @Test
    public void index(){
        boolean indexExists = template.indexExists(ArticleDocument.class);
        if (indexExists){
            template.deleteIndex(ArticleDocument.class);
        }

        template.createIndex(ArticleDocument.class);
    }

    @Test
    public void client(){
        org.elasticsearch.client.Client client = template.getClient();
    }
}
