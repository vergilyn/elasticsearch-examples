package com.vergilyn.examples.elasticsearch;

import com.vergilyn.examples.elasticsearch.document.CityDoc;
import com.vergilyn.examples.elasticsearch.repository.CityRepository;
import com.vergilyn.examples.elasticsearch.service.CityService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/7/5
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticBasicApplication.class)
public class CityDocTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityDocTest.class);
    @Autowired
    private CityService service;
    @Autowired
    private CityRepository repository;
    private CityDoc cq;
    private CityDoc sc;
    private CityDoc bj;

    @Before
    public void before(){
        cq = new CityDoc(1L, "重庆", "重庆的描述", 10);
        sc = new CityDoc(2L, "四川", "四川的描述", 9);
        bj = new CityDoc(null, "北京", "北京的描述", 5);
    }

    @Test
    public void save() {
        Long cqId = service.save(cq);
        Assert.assertEquals(cq.getId(), cqId);

        Long bjId = service.save(bj);
        System.out.println("bj ID: " + bjId);
    }
}
