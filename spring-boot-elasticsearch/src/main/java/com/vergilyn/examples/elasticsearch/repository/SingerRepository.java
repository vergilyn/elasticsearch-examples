package com.vergilyn.examples.elasticsearch.repository;

import com.vergilyn.examples.elasticsearch.document.SingerDoc;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/14
 */
@Repository
public interface SingerRepository extends ElasticsearchRepository<SingerDoc,Long> {

}
