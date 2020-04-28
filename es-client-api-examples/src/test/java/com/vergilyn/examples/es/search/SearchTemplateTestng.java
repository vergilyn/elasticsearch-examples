package com.vergilyn.examples.es.search;

import java.io.IOException;
import java.util.Map;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

/**
 * @author vergilyn
 * @date 2020-04-27
 */
@Slf4j
public class SearchTemplateTestng extends AbstractEsApiTestng {

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/search-template.html">search-template.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-search-template.html">java-rest-high-search-template.html</a>
     */
    @Test
    public void createSearchTemplate() throws IOException {
        // TODO 2020-04-27
    }

    /**
     * <pre>
     *   Search templates can be registered in advance through stored scripts API.
     *   <strong>Note that the stored scripts API is not yet available in the high-level REST client,
     *   so in this example we use the low-level REST client.</strong>
     * </pre>
     *
     * @see com.vergilyn.examples.es.cases.ArticleSearchTestng#registeredSearchTemplate()
     */
    @Test
    public void registeredTemplate() throws IOException {
        Request scriptRequest = new Request("POST", "_scripts/title_search");
        scriptRequest.setJsonEntity(
                "{" +
                        "  \"script\": {" +
                        "    \"lang\": \"mustache\"," +
                        "    \"source\": {" +
                        "      \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                        "      \"size\" : \"{{size}}\"" +
                        "    }" +
                        "  }" +
                        "}");
        Response scriptResponse = restClient.performRequest(scriptRequest);

    }

    /**
     * <pre>
     *   curl -XGET "http://127.0.0.1:9200/_scripts/{search-template-name}"
     * </pre>
     *
     * @see com.vergilyn.examples.es.cases.ArticleSearchTestng#getSearchTemplateScript()
     */
    @Test
    public void getSearchTemplateScript() throws IOException {

    }

    /**
     * FIXME 2020-04-27
     * search-template 通过kibana添加到elasticsearch
     */
    @Test
    public void searchTemplate() throws IOException {
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest(ES_INDEX));

        request.setScriptType(ScriptType.STORED);
        request.setScript(ES_INDEX_TEMPLATE);

        Map<String, Object> scriptParams = Maps.newHashMap();
        request.setScriptParams(scriptParams);

        SearchTemplateResponse response = rhlClient.searchTemplate(request, RequestOptions.DEFAULT);
        log.info("search-template >>>> SUCCESS, response: {}", prettyPrintJson(response));
    }

}
