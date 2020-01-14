package com.vergilyn.examples.es.repository;

import java.io.Serializable;
import java.util.List;

import org.elasticsearch.common.unit.TimeValue;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/6/23
 */
public interface EsCrudRepository<T extends AbstractDocument, ID extends Serializable> {
    <S extends T> S save(S entity);

    <S extends T> List<S> save(List<S> entities);

    <S extends T> S get(ID id);

    <S extends T> List<S> get(List<ID> ids);

    boolean delete(ID id);

    /**
     * @return 返回删除失败的id集合
     */
    List<ID> delete(List<ID> ids);

    Class<T> getEntityClass();

    TimeValue getTimeout();
}
