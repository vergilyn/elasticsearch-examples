package com.vergilyn.examples.elasticsearch.service.impl;

import java.util.List;

import com.vergilyn.examples.elasticsearch.document.CityDoc;
import com.vergilyn.examples.elasticsearch.repository.CityRepository;
import com.vergilyn.examples.elasticsearch.service.CityService;

import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/7/5
 */
@Service
public class CityServiceImpl implements CityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CityServiceImpl.class);

    /* 分页参数 */
    private static final Integer PAGE_SIZE = 10;          // 每页数量
    private static final Integer DEFAULT_PAGE_NUMBER = 0; // 默认当前页码

    /* 搜索模式 */
    private static final String SCORE_MODE_SUM = "sum"; // 权重分求和模式
    private static final Float  MIN_SCORE = 10.0F;      // 由于无相关性的分值默认为 1 ，设置权重分最小值为 10

    @Autowired
    private CityRepository cityRepository; // ES 操作类

    @Override
    public Long save(CityDoc city) {
        CityDoc cityResult = cityRepository.save(city);
        return cityResult.getId();
    }

    @Override
    public List<CityDoc> search(Integer pageNumber, Integer pageSize, String searchContent) {

        // 校验分页参数
        if (pageSize == null || pageSize <= 0) {
            pageSize = PAGE_SIZE;
        }

        if (pageNumber == null || pageNumber < DEFAULT_PAGE_NUMBER) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        LOGGER.info("\n searchCity: searchContent [" + searchContent + "] \n ");

        // 构建搜索查询
        SearchQuery searchQuery = getCitySearchQuery(pageNumber,pageSize,searchContent);

        LOGGER.info("\n searchCity: searchContent [" + searchContent + "] \n DSL  = \n " + searchQuery.getQuery().toString());

        Page<CityDoc> cityPage = cityRepository.search(searchQuery);
        return cityPage.getContent();
    }

    /**
     * 根据搜索词构造搜索查询语句
     *
     * 代码流程：
     *      - 权重分查询
     *      - 短语匹配
     *      - 设置权重分最小值
     *      - 设置分页参数
     *
     * @param pageNumber 当前页码
     * @param pageSize 每页大小
     * @param searchContent 搜索内容
     * @return
     */
    private SearchQuery getCitySearchQuery(Integer pageNumber, Integer pageSize,String searchContent) {
        // 短语匹配到的搜索词，求和模式累加权重分
        // 权重分查询 https://www.elastic.co/guide/cn/elasticsearch/guide/current/function-score-query.html
        //   - 短语匹配 https://www.elastic.co/guide/cn/elasticsearch/guide/current/phrase-matching.html
        //   - 字段对应权重分设置，可以优化成 enum
        //   - 由于无相关性的分值默认为 1 ，设置权重分最小值为 10

        // FIXME 2019-06-05 高版本spring-data-elasticsearch不知道如何修改
        FunctionScoreQueryBuilder functionScoreQueryBuilder = null;/*QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchPhraseQuery("name", searchContent),
                        ScoreFunctionBuilders.weightFactorFunction(1000))
                .add(QueryBuilders.matchPhraseQuery("description", searchContent),
                        ScoreFunctionBuilders.weightFactorFunction(500))
                .scoreMode(SCORE_MODE_SUM).setMinScore(MIN_SCORE);*/

        // 分页参数
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        return new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
    }

    @Override
    public List<CityDoc> findByDescriptionAndScore(String description, Integer score) {
        return cityRepository.findByDescriptionAndScore(description, score);
    }

    @Override
    public List<CityDoc> findByDescriptionOrScore(String description, Integer score) {
        return cityRepository.findByDescriptionOrScore(description, score);
    }

    @Override
    public List<CityDoc> findByDescription(String description, Pageable pageable) {
        return cityRepository.findByDescription(description, pageable).getContent();
    }

    @Override
    public List<CityDoc> findByDescriptionNot(String description, Pageable pageable) {
        return cityRepository.findByDescriptionNot(description, pageable).getContent();
    }

    @Override
    public List<CityDoc> findByDescriptionLike(String description, Pageable pageable) {
        return cityRepository.findByDescriptionLike(description, pageable).getContent();
    }

}
