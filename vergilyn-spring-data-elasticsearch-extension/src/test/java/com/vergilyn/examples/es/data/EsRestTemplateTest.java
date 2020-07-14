package com.vergilyn.examples.es.data;

import java.util.List;

import com.vergilyn.examples.es.data.document.ArticleDocument;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.testng.collections.Lists;

import static com.vergilyn.examples.es.data.document.ArticleDocument.STATUS_DRAFT;
import static com.vergilyn.examples.es.data.document.ArticleDocument.STATUS_PUBLISH;

/**
 * <a href="https://docs.spring.io/spring-data/elasticsearch/docs/3.2.7.RELEASE/reference/html/#elasticsearch.operations.resttemplate">ElasticsearchRestTemplate</a>
 * 底层基于<a href="https://docs.spring.io/spring-data/elasticsearch/docs/3.2.7.RELEASE/reference/html/#elasticsearch.clients.rest">High Level REST Client.</a>
 * @author vergilyn
 * @date 2020-05-03
 */
@Slf4j
public class EsRestTemplateTest extends AbstractEsSpringBootTest {
    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Test
    public void test(){
        boolean indexExists = restTemplate.indexExists(ArticleDocument.class);

        Assert.assertTrue(indexExists);

    }

    @Test
    public void rhlClient(){
        RestHighLevelClient rhlClient = restTemplate.getClient();
    }

    /**
     * FIXME 2020-05-06 {@linkplain DefaultResultMapper#mapResults(org.elasticsearch.action.search.SearchResponse, java.lang.Class, org.springframework.data.domain.Pageable)}
     * `java.lang.NoSuchMethodError: org.elasticsearch.search.SearchHits.getTotalHits()J`
     * es-v7.6.2 与 spring-data-es-v3.2.3 不兼容
     */
    @Test
    public void queryForList(){
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();

        TermsQueryBuilder filter = QueryBuilders.termsQuery("status", Lists.newArrayList(STATUS_DRAFT, STATUS_PUBLISH));

        SearchQuery search = new NativeSearchQuery(query, filter);
        search.addSort(Sort.by("id"));
        // search.addSourceFilter();
        search.setPageable(PageRequest.of(0, 2));

        List<ArticleDocument> rs = restTemplate.queryForList(search, ArticleDocument.class);

        // System.out.printf("queryForList() >>>> %s \r\n", JSON.toJSONString(rs, SerializerFeature.PrettyFormat));
        System.out.printf("queryForList() >>>> %s \r\n", rs);
    }
}
