package com.vergilyn.examples.es.document;

import java.io.IOException;
import java.time.LocalDateTime;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-update.html">docs-update.html</a>
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-document-update.html">java-rest-high-document-update.html</a>
 */
@Slf4j
public class UpdateDocsTestng extends AbstractEsApiTestng {
    protected static final String X_ID = "1234";

    /**  更新document(重索引document)，只要document被更新，就会reindex。
     *  <pre>
     *  1. updates with a partial document >>>> <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-update.html#_updates_with_a_partial_document">docs-update.html#_updates_with_a_partial_document</a>
     *  curl -XPOST "http://127.0.0.1:9200/{index_name}/_update/2" -H 'Content-Type: application/json' -d'
     *  {
     *      "doc":{
     *          "online_time": "2018-06-18 23:00:00"
     *      }
     *  }'
     *  备注：若update未改变任何field，那么默认情况下会ignore此次request，返回`result: noop`；（是否意味着并不会reindex-document？）可以通过`detect_noop: false`来禁用此功能。
     *  (详见：<a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docs-update.html#_detecting_noop_updates">docs-update.html#_detecting_noop_updates</a>)
     *
     *  2. op_type: create(default)、index，create: re-create-exception；index: add or replace a document。
     *  curl -XPUT "http://127.0.0.1:9200/{index_name}/4?op_type=index" -H 'Content-Type: application/json' -d'
     *  {
     *      "online_time": "2018-06-14 19:00:00"
     *  }'
     *
     *  3. <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/docs-update-by-query.html">docs-update-by-query.html</a>
     *    will incr _version(所以可以用来reindex-document)
     *  curl -XPOST "http://127.0.0.1:9200/{index_name}/_update_by_query?conflicts=proceed" -H 'Content-Type: application/json' -d'
     *  {
     *      "query":{
     *          "type":{
     *              "value":"source_type_all"
     *          }
     *      }
     *  }'
     *  备注：如何批量更新某个field，不能`doc`，但可以`script`？
     *
     *  4. _bulk
     *  </pre>
     *
     *  v-note：如果只是为了reindex-document，完全可以只用`_update_by_query`; 若要批量更新，(暂时)觉得`_bulk`是最高效的。
     */
    /**
     * except: only update, not create.
     */
    @Test
    public void updateSync() throws IOException {

        final UpdateRequest request = new UpdateRequest(ES_INDEX, X_ID)
                .timeout(TimeValue.timeValueMinutes(2))
                .docAsUpsert(false)
                // .fetchSource(true)
                .doc(buildData(null, "updated content.", null));

        final UpdateResponse response = rhlClient.update(request, RequestOptions.DEFAULT);
        log.info("update docs >>>> SUCCESS, response: {}", prettyPrintJson(response));

        getById(X_ID);
    }

    /**
     * except: update or insert.
     * <p>upsert:
     *   If the document does not already exist, it is possible to define some content
     *   that will be inserted as a new document using the upsert method
     */
    @Test
    public void upsertSync() throws IOException {

        final UpdateRequest request = new UpdateRequest(ES_INDEX, X_ID)
                .timeout(TimeValue.timeValueMinutes(2))
                .docAsUpsert(true)
                // .fetchSource(true)
                .doc(buildData("vergilyn", "upsert docs.", LocalDateTime.now()));

        final UpdateResponse response = rhlClient.update(request, RequestOptions.DEFAULT);
        log.info("upsert docs >>>> SUCCESS, response: {}", prettyPrintJson(response));

        getById(X_ID);
    }

    public void getById(String id){
        try {
            GetResponse response = rhlClient.get(new GetRequest(ES_INDEX, id), RequestOptions.DEFAULT);

            log.info("document[{}] >>>> {}", id, prettyPrintJson(response));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
