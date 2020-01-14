package com.vergilyn.examples.elasticsearch.service.impl;

import java.util.List;
import java.util.Map;

import com.vergilyn.examples.elasticsearch.constant.ElasticConstant;
import com.vergilyn.examples.elasticsearch.document.SingerDoc;
import com.vergilyn.examples.elasticsearch.repository.SingerRepository;
import com.vergilyn.examples.elasticsearch.service.SingerService;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.WeightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/14
 */
@Service
@Slf4j
public class SingerServiceImpl implements SingerService {

    @Autowired
    SingerRepository singerRepository;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public SingerDoc save(SingerDoc singerDoc) {
        return singerRepository.save(singerDoc);
    }

    @Override
    public Iterable<SingerDoc> saves(List<SingerDoc> singerDocs) {
        return singerRepository.saveAll(singerDocs);
    }

    @Override
    public List<SingerDoc> search(Integer pageNumber, Integer pageSize, String searchKeyword) {
        // 分页参数
        Pageable pageable = new PageRequest(pageNumber, pageSize);

        // Function Score Query
        // FIXME 2019-06-05 高版本的spring-data-elasticsearch 不知道怎么写，待修正
        QueryBuilder functionScoreQueryBuilder = null;/*QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", searchKeyword))
                        , ScoreFunctionBuilders.weightFactorFunction(1000))
                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("talentAgency", searchKeyword))
                        , ScoreFunctionBuilders.weightFactorFunction(1000))
                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("desc", searchKeyword))
                        , ScoreFunctionBuilders.weightFactorFunction(100));*/

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[3];
        ScoreFunctionBuilder<WeightBuilder> scoreFunctionBuilder = new WeightBuilder();
        scoreFunctionBuilder.setWeight(1000);
        FunctionScoreQueryBuilder.FilterFunctionBuilder name =
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("name", searchKeyword), scoreFunctionBuilder);

        filterFunctionBuilders[0] = name;

        // 创建搜索 DSL 查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(ElasticConstant.ElasticIndex.SINGER.getName())
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder)
                .build();

        log.info("searchSinger(): searchKeyword [" + searchKeyword + "] , DSL >>>> " + searchQuery.getQuery().toString());

        Page<SingerDoc> searchPageResults = singerRepository.search(searchQuery);
        return searchPageResults.getContent();
    }

    @Override
    public List<SingerDoc> allSinger() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(ElasticConstant.ElasticIndex.SINGER.getName())
                .build();
        return singerRepository.search(searchQuery).getContent();
    }

    @Override
    public Map singerMapping() {
        return elasticsearchTemplate.getMapping(ElasticConstant.ElasticIndex.SINGER.getName()
                , ElasticConstant.ElasticType.SINGER.getName());
    }
}
