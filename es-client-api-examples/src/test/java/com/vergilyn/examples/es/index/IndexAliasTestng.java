package com.vergilyn.examples.es.index;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-27
 */
@Slf4j
public class IndexAliasTestng extends AbstractEsApiTestng {

    /**
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-add-alias.html">indices-add-alias.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-update-aliases.html">java-rest-high-update-aliases.html</a>
     */
    @Test
    public void addIndexAlias() throws IOException {
        IndicesAliasesRequest request = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions aliasAction =
                new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                        .index(ES_INDEX)
                        .alias(ES_INDEX_ALIAS);
        request.addAliasAction(aliasAction);

        AcknowledgedResponse response = rhlClient.indices().updateAliases(request, RequestOptions.DEFAULT);

        log.info("add-index-alias >>>> SUCCESS, response: {}", prettyPrintJson(response));
    }
}
