package com.vergilyn.examples.elasticsearch.repository;

import java.util.List;

import com.vergilyn.examples.elasticsearch.document.NewsInfoDoc;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/14
 */
@Repository
public interface NewsInfoRepository extends ElasticsearchRepository<NewsInfoDoc, Long> {
    /** Caused by: org.elasticsearch.index.query.QueryParsingException: No query registered for [_source]
     * 去掉_source参数则可以
    {
        "_source": {
            "excludes": ["infoContent"]
        },
        "query": {
            "bool": {
                "must": [{
                    "query_string": {
                        "fields": ["listTitle", "infoKeywords", "infoTitle", "infoContent"],
                        "query": "?0",
                        "default_operator": "and"
                    }
                }],
                "must_not": [],
                "should": [],
                "filter": [
                    {"term": {"dataStatus": "?1"}}
                ]
            }
        },
        "from": 0,
        "size": 10,
        "sort": [],
        "aggs": {}
    }
    */
    @Query("{\"query\":{\"bool\":{\"must\":[{\"query_string\":{\"fields\":[\"listTitle\",\"infoKeywords\",\"infoTitle\",\"infoContent\"],\"query\":\"?0\",\"default_operator\":\"and\"}}],\"must_not\":[],\"should\":[],\"filter\":[{\"term\":{\"dataStatus\":\"?1\"}}]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}")
    List<NewsInfoDoc> search(String queryString, String dataStatus);
}
