package com.vergilyn.examples.api;

import java.io.IOException;
import java.util.Map;

import com.vergilyn.examples.HighRestClientESApplication;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import org.testng.collections.Maps;

/**
 * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.4/search-template.html">search-template</a>
 * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.4/java-rest-high-search-template.html">java-rest-high-search-template.html</a>
 * @author VergiLyn
 * @date 2019-07-22
 */
@SpringBootTest(classes= HighRestClientESApplication.class)
public class SearchTemplateTest extends AbstractTestNGSpringContextTests {
    private Map<String, Object> scriptParams = Maps.newHashMap();
    @Autowired
    private RestHighLevelClient rhlClient;

    @BeforeTest
    protected void before(){
        scriptParams.put("from", 0);
        scriptParams.put("size", 10);
        scriptParams.put("query_string", "独家视频");
        scriptParams.put("range_online_time_to", "2019-07-22 09:28:06");
        scriptParams.put("multi_match_fields", Lists.newArrayList("content^0.2", "info_title^0.8", "keywords^1.2", "list_title^0.9", "synopsis^0.6"));
    }

    /**
     * search-template通过kibana添加到elasticsearch，其search-template-name = app_data_x_search_template。
     */
    @Test
    public void searchTemplate(){
        String template = "app_data_x_search_template";
        String index = "app_data_1_alias";

        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest(index));

        request.setScriptType(ScriptType.STORED);
        request.setScript(template);

        request.setScriptParams(scriptParams);

        try {
            SearchTemplateResponse response = rhlClient.searchTemplate(request, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
