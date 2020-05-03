package com.vergilyn.examples.es.data;

import java.time.LocalDate;

import com.vergilyn.examples.es.data.autoconfigure.DynamicIndexStrategy;
import com.vergilyn.examples.es.data.document.DynamicIndexDocument;
import com.vergilyn.examples.es.data.repository.DynamicIndexRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @author vergilyn
 * @date 2020-05-03
 */
@Slf4j
public class DynamicIndexDocumentTest extends AbstractEsSpringBootTest {
    @Autowired
    private DynamicIndexRepository repository;
    @Autowired
    private DynamicIndexStrategy strategy;
    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Test
    public void createIndex(){
        LocalDate now = strategy.getNow();

        // today
        String todayIndex = strategy.indexName();
        restTemplate.createIndex(DynamicIndexDocument.class);

        Assert.assertTrue(restTemplate.indexExists(todayIndex));

        // tomorrow
        strategy.setNow(now.plusDays(1));
        String tomorrowIndex = strategy.indexName();
        restTemplate.createIndex(DynamicIndexDocument.class);

        Assert.assertTrue(restTemplate.indexExists(tomorrowIndex));
    }
}
