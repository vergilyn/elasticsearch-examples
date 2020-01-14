
package com.vergilyn.examples.elasticsearch.service;

import java.util.List;

import com.vergilyn.examples.elasticsearch.document.CityDoc;

import org.springframework.data.domain.Pageable;

public interface CityService {

    /** 新增 ES 城市信息 */
    Long save(CityDoc city);

    /**
     * 搜索词搜索，分页返回城市信息
     */
    List<CityDoc> search(Integer pageNumber, Integer pageSize, String searchContent);

    /**
     * AND 语句查询
     */
    List<CityDoc> findByDescriptionAndScore(String description, Integer score);

    /**
     * OR 语句查询
     */
    List<CityDoc> findByDescriptionOrScore(String description, Integer score);

    /**
     * 查询城市描述
     */
    List<CityDoc> findByDescription(String description, Pageable pageable);

    /**
     * NOT 语句查询
     */
    List<CityDoc> findByDescriptionNot(String description, Pageable pageable);

    /**
     * LIKE 语句查询
     */
    List<CityDoc> findByDescriptionLike(String description, Pageable pageable);
}