package com.vergilyn.examples.es.index;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-27
 *
 */
public class CreateIndexTemplateTestng extends AbstractEsApiTestng {

    /**
     * <pre>
     *   curl -X PUT "localhost:9200/_template/{index_template_name}?pretty" -H 'Content-Type: application/json' -d'
     *   {
     *     "index_patterns": ["vergilyn-es-client-api-examples*"],
     *     "settings": {
     *       "number_of_shards": 1
     *     },
     *     "mappings": {
     *       "_source": {
     *         "enabled": false
     *       },
     *       "properties": {
     *         "username": {"type": "keyword"},
     *         "content": {"type": "text"},
     *         "update_time": {
     *           "type": "date",
     *           "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||strict_date_optional_time||epoch_millis"
     *         }
     *       }
     *     },
     *     "aliases": {
     *          "{index}_alias" : {}
     *     }
     *   }'
     * </pre>
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.6/indices-templates.html">indices-templates.html</a>
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-create-index.html">java-rest-high-create-index.html</a>
     */
    @Test
    public void sync(){
        // TODO 2020-04-27
    }
}
