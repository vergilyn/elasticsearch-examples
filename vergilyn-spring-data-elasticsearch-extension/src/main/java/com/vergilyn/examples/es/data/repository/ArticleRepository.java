package com.vergilyn.examples.es.data.repository;

import com.vergilyn.examples.es.data.document.ArticleDocument;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

/**
 * @author vergilyn
 * @date 2020-04-30
 */
public interface ArticleRepository extends ElasticsearchCrudRepository<ArticleDocument, Long> {
}
