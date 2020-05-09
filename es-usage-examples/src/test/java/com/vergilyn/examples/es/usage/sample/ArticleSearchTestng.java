package com.vergilyn.examples.es.usage.sample;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.vergilyn.examples.es.AbstractEsClientTestng;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

/**
 * @author vergilyn
 * @date 2020-04-28
 */
@Slf4j
public class ArticleSearchTestng extends AbstractEsClientTestng {
    private static final String ES_INDEX_SOURCE_RESOURCE_PATH = "vergilyn-article_index_source.json";
    private static final String ES_INDEX_DOCS_RESOURCE_PATH = "vergilyn-article_index_docs.json";
    /** FIXME 2020-04-28 search-template动态拼接条件中的","判断存在问题 */
    private static final String ES_SEARCH_TEMPLATE_SOURCE_RESOURCE_PATH = "vergilyn-article_search_template_script.json";
    private static final String ES_INDEX = "vergilyn-article";
    private static final String ES_INDEX_ALIAS = ES_INDEX + "_alias";
    private static final String ES_SEARCH_TEMPLATE = ES_INDEX + "_search_template";

    /**
     * @see `vergilyn-article_search_template_script.json`
     */
    @Test
    public void search() throws IOException {
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest(ES_INDEX_ALIAS));
        request.setScript(ES_SEARCH_TEMPLATE);
        request.setScriptType(ScriptType.STORED);

        Map<String, Object> scriptParams = Maps.newHashMap();
        scriptParams.put("from", 0);
        scriptParams.put("size", 3);
        scriptParams.put("timeout", "2m");  // <=> TimeValue.timeValueMinutes(2L).toString();
        scriptParams.put("query_string", "重庆");
        scriptParams.put("multi_match_fields", new String[]{"title^0.8", "list_title^1.0"});

        // dynamic params: range_publish_time_gte、range_publish_time_lte、status、tags
        scriptParams.put("range_publish_time_gte", "2020-04-28 12:00:00");
        scriptParams.put("status", new Byte[]{ArticleDto.STATUS_PUBLISH});
        scriptParams.put("tags", new String[]{"1"});

        request.setScriptParams(scriptParams);

        SearchTemplateResponse response = rhlClient.searchTemplate(request, RequestOptions.DEFAULT);
        log.info("search-template result >>>> {}", prettyPrintJson(response));
    }


    @Test
    public void reset() throws IOException {
        try {
            restClient.performRequest(new Request("DELETE", "/" + ES_INDEX));
        } catch (IOException e) {
            // do nothing
        }

        try {
            restClient.performRequest(new Request("DELETE", "_scripts/" + ES_SEARCH_TEMPLATE));
        } catch (IOException e) {
            // do nothing
        }

        createIndex();

        bulkCreateDocs();

        registeredSearchTemplate();

        log.info("RESET >>>> SUCCESS");
    }

    @Test
    public void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(ES_INDEX);
        request.source(getIndexSource(), XContentType.JSON);
        request.alias(new Alias(ES_INDEX_ALIAS));

        CreateIndexResponse createIndexResponse = rhlClient.indices().create(request, RequestOptions.DEFAULT);
        log.info("create index[{}] >>>> SUCCESS, response: {}", ES_INDEX, createIndexResponse.isAcknowledged());
    }

    @Test
    public void bulkCreateDocs() throws IOException {
        String dataJson = getIndexDocs();
        JsonNode arts = objectMapper.readTree(dataJson);

        BulkRequest bulkRequest = new BulkRequest(ES_INDEX);
        arts.forEach(art -> {
            IndexRequest indexRequest = new IndexRequest()
                    .opType(DocWriteRequest.OpType.CREATE)
                    .id(art.get("id").asText())
                    .source(art);

            bulkRequest.add(indexRequest);
        });

        BulkResponse bulkResponse = rhlClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        log.info("bulk-create-docs >>>> SUCCESS, response: hasFailures[{}]", bulkResponse.hasFailures());
    }

    /**
     * IMPORTANT >>>> template-script 中有些地方必须保留空格，否则解析会有问题
     * EX.
     *  error -> "filter":[{"range":{"publish_time":{{{#time_gte}}"gte":"{{time_gte}}"{{/time_gte}}....
     *  right -> "filter":[{"range":{"publish_time":{ {{#time_gte}}"gte":"{{time_gte}}"{{/time_gte}}....
     */
    @Test
    public void registeredSearchTemplate() throws IOException {
        String source = getSearchTemplateSource();
        // 压缩，并转义
        source = source.replaceAll("\r|\n|\t", "");
        // source = source.replaceAll(" ", "");  // 有些空格必须保留
        source = StringEscapeUtils.escapeJava(source);

        Request scriptRequest = new Request("POST", "_scripts/" + ES_SEARCH_TEMPLATE);
        String script = "{" +
                "  \"script\": {" +
                "    \"lang\": \"mustache\"," +
                "    \"source\": \"" + source + "\"" +
                "  }" +
                "}";

        scriptRequest.setJsonEntity(script);
        Response response = restClient.performRequest(scriptRequest);

        log.info("registered search template >>>> SUCCESS, response >>>> {}", response.getStatusLine().getStatusCode());
    }

    /**
     * <pre>
     *   curl -XGET "http://127.0.0.1:9200/_scripts/{search-template-name}"
     * </pre>
     */
    @Test
    public void getSearchTemplateScript() throws IOException {
        Request scriptRequest = new Request("GET", "_scripts/" + ES_SEARCH_TEMPLATE);

        Response response = restClient.performRequest(scriptRequest);

        log.info("get search template script >>>> SUCCESS, response: {}", response.getStatusLine().getStatusCode());
    }

    @Test
    public void deleteIndex() throws IOException {
        AcknowledgedResponse response = rhlClient.indices()
                            .delete(new DeleteIndexRequest(ES_INDEX), RequestOptions.DEFAULT);

        log.info("delete index[{}] >>>> SUCCESS, response: {}", ES_INDEX, prettyPrintJson(response));
    }

    public String getIndexSource() throws IOException {
        return getResource(ES_INDEX_SOURCE_RESOURCE_PATH);
    }

    public String getIndexDocs() throws IOException {
        return getResource(ES_INDEX_DOCS_RESOURCE_PATH);
    }

    public String getSearchTemplateSource() throws IOException {
        return getResource(ES_SEARCH_TEMPLATE_SOURCE_RESOURCE_PATH);
    }

}
