package com.vergilyn.examples.es.api;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.programcreek.com/java-api-examples/?class=org.elasticsearch.common.xcontent.ToXContent&method=toXContent">programcreek.com ToXContent examples</a>
 */
@Slf4j
public class XContentTestng extends AbstractEsApiTestng {
    private GetResponse response;

    @BeforeTest
    protected void beforeTest() throws IOException {
        response = rhlClient.get(new GetRequest(ES_INDEX, ID), RequestOptions.DEFAULT);
    }

    /**
     * @see org.elasticsearch.common.xcontent.XContentBuilder
     * @see org.elasticsearch.common.xcontent.XContentFactory
     * @see org.elasticsearch.common.Strings
     * @see org.elasticsearch.common.bytes.BytesReference
     * @see org.elasticsearch.common.xcontent.XContentHelper
     */
    @Test
    public void test() throws IOException {

        System.out.printf("Response.toString() >>>> %s \r\n", response.toString());

        XContentBuilder xContentBuilder = response.toXContent(XContentFactory.jsonBuilder(), ToXContent.EMPTY_PARAMS);
        System.out.printf("XContentBuilder.toString() >>>> %s \r\n", xContentBuilder.toString());  // <=> Object.toString();

        // TODO 2020-04-26 并未格式化输出的json
        BytesReference bytesReference = BytesReference.bytes(xContentBuilder);
        System.out.printf("BytesReference.utf8ToString() >>>> %s \r\n", bytesReference.utf8ToString());

        // <=> BytesReference.bytes(xContentBuilder).utf8ToString();
        String json = Strings.toString(xContentBuilder);
        System.out.printf("Strings.toString() >>>> %s \r\n", json);

        // 格式化输出json  TODO 2020-04-26 字段排序输出
        String prettyJson = Strings.toString(response, true, true);
        System.out.printf("Strings.toString() >>>> %s \r\n", prettyJson);

        String xcontentHelper = XContentHelper.convertToJson(bytesReference, true, XContentType.JSON);
        System.out.printf("XContentHelper.convertToJson() >>>> %s \r\n", xcontentHelper);
    }

    /**
     * except: json-field 有序
     * package 都是 `search.sort`，感觉不是期望的排序
     * @see org.elasticsearch.search.sort.SortBuilders
     * @see org.elasticsearch.search.sort.SortBuilder
     * @see org.elasticsearch.search.sort.FieldSortBuilder
     */
    @Test
    public void sortBuilder(){

    }
}
