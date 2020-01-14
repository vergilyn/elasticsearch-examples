package com.vergilyn.examples.elasticsearch.repository;

import java.util.List;

import com.vergilyn.examples.elasticsearch.document.CityDoc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends ElasticsearchRepository<CityDoc, Long> {

    /**
     * 查询城市描述，等同于注解（可能page参数需要修改）：@Query("{\"bool\" : {\"must\" : {\"term\" : {\"description\" : \"?0\"}}}}")
     */
    Page<CityDoc> findByDescription(String description, Pageable page);

    /**
     * AND 语句查询
     */
    List<CityDoc> findByDescriptionAndScore(String description, Integer score);

    /**
     * OR 语句查询
     */
    List<CityDoc> findByDescriptionOrScore(String description, Integer score);

    /**
     * NOT 语句查询
     */
    Page<CityDoc> findByDescriptionNot(String description, Pageable page);

    /**
     * LIKE 语句查询
     */
    Page<CityDoc> findByDescriptionLike(String description, Pageable page);

}
