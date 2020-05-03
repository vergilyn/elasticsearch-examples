package com.vergilyn.examples.es.data.repository;

import com.vergilyn.examples.es.data.document.DynamicIndexDocument;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

/**
 * @author vergilyn
 * @date 2020-05-03
 */
public interface DynamicIndexRepository extends ElasticsearchCrudRepository<DynamicIndexDocument, String> {
}
