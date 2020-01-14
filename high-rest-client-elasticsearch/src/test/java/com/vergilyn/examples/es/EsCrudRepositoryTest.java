package com.vergilyn.examples.es;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.vergilyn.examples.HighRestClientESApplication;
import com.vergilyn.examples.es.document.TestIndexDocument;
import com.vergilyn.examples.es.repository.TestEsCrudRepository;
import com.vergilyn.examples.es.repository.annotation.EsDocument;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/6/23
 */
@SpringBootTest(classes=HighRestClientESApplication.class)
public class EsCrudRepositoryTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private TestEsCrudRepository repository;
    @Autowired
    private RestHighLevelClient rhlClient;

    /**
     * 等价于：new ClassPathResource("elastic/es_test_index.json");
     */
    @Value("classpath:elastic/es_test_index.json")
    private Resource esTestIndex;

    private TestIndexDocument doc1;
    private TestIndexDocument doc2;

    @BeforeGroups(groups = {"save"})
    public void before(){
        EsDocument esDocument = TestIndexDocument.class.getAnnotation(EsDocument.class);
        String index = esDocument.index();

        doc1 = new TestIndexDocument("1", "desc_smart", "desc_max");
        doc2 = new TestIndexDocument("2", "desc_smart2", "desc_max2");

        try {

            boolean exists = rhlClient.indices().exists(new GetIndexRequest().indices(index));
            if (exists){
                rhlClient.indices().delete(new DeleteIndexRequest(index));
            }


            String source = FileUtils.readFileToString(esTestIndex.getFile(), "utf-8");
            CreateIndexRequest create = new CreateIndexRequest(index).source(source, XContentType.JSON);
            rhlClient.indices().create(create);

            System.out.println("before-groups >>>> init successful \r\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test(groups = {"save"})
    public void save(){
        try {
            System.out.printf("before save: %s \r\n", JSON.toJSONString(doc1)).println();
            repository.save(doc1);

            doc1 = repository.get(doc1.getMetaId());
            System.out.printf("get: %s \r\n", JSON.toJSONString(doc1)).println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(groups = {"save"})
    public void saveAll(){
        try {
            List<TestIndexDocument> list = Lists.newArrayList(doc1, doc2);
            System.out.printf("before saveAll: %s \r\n", JSON.toJSONString(list)).println();
            repository.save(list);

            list = repository.get(Lists.newArrayList("1", "2", "3"));
            System.out.printf("get list: %s \r\n", JSON.toJSONString(list)).println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(dependsOnMethods = {"save", "saveAll"})
    public void delete(){
        System.out.printf("delete: %s \r\n", repository.delete(doc1.getMetaId())).println();
    }

    @Test(dependsOnMethods = {"save", "saveAll"})
    public void deleteMulti(){
        System.out.printf("multi-delete: %s \r\n", repository.delete(Lists.newArrayList("1", "2", "3"))).println();
    }
}
