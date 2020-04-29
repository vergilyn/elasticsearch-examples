package com.vergilyn.examples.es.repository.core;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/7/5
 * @see org.springframework.data.elasticsearch.core.ResultsMapper
 */
public interface ResultsMapper extends SearchResultMapper, GetResultMapper, MultiGetResultMapper {

    EntityMapper getEntityMapper();
}
