package com.vergilyn.examples.elasticsearch.service;

import com.vergilyn.examples.elasticsearch.document.NewsInfoDoc;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/17
 */
public interface NewsInfoService {

    void save(NewsInfoDoc info);

    Boolean addAlias();

    Object search(String queryString, SearchFunction searchFunction);

    public enum SearchFunction{
        SPRING_JAVA,SPRING_ANNOTATION,ELASTIC
    }
}

