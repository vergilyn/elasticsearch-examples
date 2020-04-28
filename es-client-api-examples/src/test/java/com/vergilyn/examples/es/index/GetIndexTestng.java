package com.vergilyn.examples.es.index;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-28
 */
@Slf4j
public class GetIndexTestng extends AbstractEsApiTestng {

    /**
     * <pre>
     *   curl -XGET "http://127.0.0.1:9200/{index-name}"
     * </pre>
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-get-index.html">indices-get-index.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-get-index.html">java-rest-high-get-index.html</a>
     */
    @Test
    public void source() throws IOException {
        GetIndexRequest request = new GetIndexRequest(ES_INDEX);

        // If true, defaults will be returned for settings not explicitly set on the index
        request.includeDefaults(true);

        // Setting `IndicesOptions` controls how unavailable indices are resolved and how wildcard expressions are expanded
        request.indicesOptions(IndicesOptions.lenientExpandOpen());

        GetIndexResponse response = rhlClient.indices()
                    .get(request, RequestOptions.DEFAULT);

        log.info("get-index-source >>>> SUCCESS, response: {}", response.toString());
    }

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-get-settings.html">indices-get-settings.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-get-settings.html">java-rest-high-get-settings.html</a>
     */
    @Test
    public void settings(){
        // TODO 2020-04-28
    }

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-get-mapping.html">indices-get-mapping.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-get-mappings.html">java-rest-high-get-mappings.html</a>
     */
    @Test
    public void mappings(){
        // TODO 2020-04-28
    }

    /**
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-get-template.html">indices-get-template.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-get-templates.html">java-rest-high-get-templates.html</a>
     */
    @Test
    public void template(){
        // TODO 2020-04-28
    }
}
