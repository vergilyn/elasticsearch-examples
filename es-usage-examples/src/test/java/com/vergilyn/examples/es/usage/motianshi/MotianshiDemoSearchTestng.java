package com.vergilyn.examples.es.usage.motianshi;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.vergilyn.examples.es.AbstractEsClientTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

/**
 * @author vergilyn
 * @date 2020-05-09
 *
 * @see <a href="https://mp.weixin.qq.com/s/C9fWBqiE6TzDXfiZmg8l9Q">十分钟学会使用 Elasticsearch 优雅搭建自己的搜索系统（附源码）</a>
 * @see <a href="https://github.com/Motianshi/alldemo/tree/master/demo-search">[github] Motianshi demo-search</a>
 */
@Slf4j
public class MotianshiDemoSearchTestng extends AbstractEsClientTestng {
    private static final String INDEX_NAME = "motianshi-demo-search";
    private static final String INDEX_NAME_ALIAS = INDEX_NAME + "_alias";
    private static final String ES_MAPPING_RESOURCE_PATH = "motianshi/es-mapping.json";
    private static final String ITEMS_DATA_PATH = "motianshi/items-data.json";

    @Test
    public void search() throws IOException {
        SearchRequest request = new SearchRequest(INDEX_NAME_ALIAS);

        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new MatchQueryBuilder("title", "眼镜"))
                .from(1)
                .size(10);
        request.source(builder);

        SearchResponse resp = rhlClient.search(request, RequestOptions.DEFAULT);

        System.out.println(prettyPrintJson(resp));
    }

    @Test
    public void deleteIndex() throws IOException {
        AcknowledgedResponse response = rhlClient.indices()
                .delete(new DeleteIndexRequest(INDEX_NAME), RequestOptions.DEFAULT);

        Assert.assertTrue(response.isAcknowledged());
    }

    @Test
    public void createIndex() throws IOException {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("index.number_of_replicas", 1);
        settings.put("index.number_of_shards", 3);
        settings.put("index.refresh_interval", "10s");
        settings.put("index.store.type", "fs");

        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME)
                            .alias(new Alias(INDEX_NAME_ALIAS))
                            .settings(settings)
                            .mapping(getResource(ES_MAPPING_RESOURCE_PATH), XContentType.JSON);

        CreateIndexResponse response = rhlClient.indices().create(request, RequestOptions.DEFAULT);
        Assert.assertTrue(response.isAcknowledged());
    }

    @Test
    public void bulkImportData() throws IOException {
        BulkRequest request = new BulkRequest(INDEX_NAME);
        buildIndexRequest(request, INDEX_NAME);

        BulkResponse resp = rhlClient.bulk(request, RequestOptions.DEFAULT);

        Assert.assertFalse(resp.hasFailures());
        Assert.assertEquals(resp.status(), RestStatus.OK);
        Assert.assertEquals(resp.getItems().length, 10_0000);
    }

    private void buildIndexRequest(BulkRequest bulkRequest, String index) throws IOException {
        String json = getResource(ITEMS_DATA_PATH);
        JsonNode jsonNode = objectMapper.readTree(json);
        if (!jsonNode.isArray()){
            return;
        }

        log.info("items-data size: {}", jsonNode.size());
        IndexRequest request;
        for (JsonNode node : jsonNode) {
            request = new IndexRequest(index);
            ItemDocument item = new ItemDocument();
            item.setItemId(node.get("商品ID").asText());
            item.setUrlId(node.get("_id").asText());

            JsonNode sellAddress = node.get("卖家地址");
            if (sellAddress != null) {
                item.setSellAddress(sellAddress.asText());
            }

            JsonNode courierFee = node.get("快递费");
            if (courierFee != null) {
                item.setCourierFee(courierFee.asText());
            }

            JsonNode promotions = node.get("优惠活动");
            if (promotions != null) {
                item.setPromotions(promotions.asText());
            }
            item.setOriginalPrice(node.get("原价").asText());
            JsonNode start = node.get("活动开始时间");
            if (start != null) {
                item.setStartTime(start.asText());
            }
            JsonNode end = node.get("活动结束时间");
            if (end != null) {
                item.setEndTime(end.asText());
            }
            item.setTitle(node.get("标题").asText());

            JsonNode guarantee = node.get("服务保障");
            if (guarantee != null) {
                item.setServiceGuarantee(guarantee.asText());
            }

            item.setVenue(node.get("会场").asText());
            JsonNode currentPrice = node.get("现价");
            if (currentPrice != null) {
                item.setCurrentPrice(currentPrice.asText());
            }

            request.source(objectMapper.writeValueAsString(item), XContentType.JSON);

            bulkRequest.add(request);
        }

    }
}
