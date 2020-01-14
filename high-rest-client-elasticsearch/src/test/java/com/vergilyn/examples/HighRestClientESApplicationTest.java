package com.vergilyn.examples;

import java.io.IOException;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * springboot-test虽然可以指定启动类{@linkplain HighRestClientESApplication}，但是并不会执行{@linkplain HighRestClientESApplication#main(String[])}。
 *
 * <p>导致application-{profile}未正常加载（可以不用{@linkplain SpringApplication#setAdditionalProfiles(String...)}，而在application.properties中配置）
 *
 * <p>另外一种方式是：{@linkplain TestPropertySource}记载配置文件，但是不支持`.yml`形式。
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/6/11
 */
@SpringBootTest(classes=HighRestClientESApplication.class)
@TestPropertySource(locations = {"/application-elastic.properties"})
public class HighRestClientESApplicationTest extends AbstractTestNGSpringContextTests{

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @DataProvider(name = "testdp", parallel = true)
    public static Object[][] testdp() {
        return new Object[][]{{"1"}, {"2"}};
    }

    /**
     * 测试单例的restHighLevelClient是否线程安全，结果：线程安全，但注意别close
     */
    @Test(enabled=true, dataProvider="testdp",threadPoolSize=2, invocationCount=0)
    public void restClient(String id){
        final GetRequest request = new GetRequest()
                .index("index")
                .type("logs")
                .id("id");

        final GetResponse response;
        try {
            response = restHighLevelClient.get(request);
            System.out.println(response.getIndex());
            System.out.println(response.getType());
            System.out.println(response.getId());
            System.out.println(response.getSourceAsString());

          //  restHighLevelClient.close();  // 因为是单例，别调用close
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}