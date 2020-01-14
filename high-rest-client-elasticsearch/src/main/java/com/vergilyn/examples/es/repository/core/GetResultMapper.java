package com.vergilyn.examples.es.repository.core;

import org.elasticsearch.action.get.GetResponse;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/7/5
 * @see org.springframework.data.elasticsearch.core.GetResultMapper
 */
public interface GetResultMapper {

    <T> T mapResult(GetResponse response, Class<T> clazz);
}
