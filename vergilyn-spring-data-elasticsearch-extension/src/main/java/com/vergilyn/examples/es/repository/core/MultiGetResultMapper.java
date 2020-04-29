package com.vergilyn.examples.es.repository.core;

import java.util.LinkedList;

import org.elasticsearch.action.get.MultiGetResponse;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/7/5
 * @see org.springframework.data.elasticsearch.core.MultiGetResultMapper
 */
public interface MultiGetResultMapper {

    <T> LinkedList<T> mapResults(MultiGetResponse responses, Class<T> clazz);
}
