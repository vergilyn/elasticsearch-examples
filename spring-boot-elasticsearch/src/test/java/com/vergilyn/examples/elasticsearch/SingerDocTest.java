package com.vergilyn.examples.elasticsearch;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.vergilyn.examples.elasticsearch.constant.SingerResources;
import com.vergilyn.examples.elasticsearch.document.SingerDoc;
import com.vergilyn.examples.elasticsearch.repository.SingerRepository;
import com.vergilyn.examples.elasticsearch.service.SingerService;

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
 * @date 2018/1/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticBasicApplication.class)
public class SingerDocTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingerDocTest.class);
    @Autowired
    SingerResources singerResources;
    @Autowired
    SingerService singerService;
    @Autowired
    SingerRepository singerRepository;

    private SingerDoc singerDoc;

    @Before
    public void init(){
        LOGGER.debug("init import 'init-singer.json' begin....");
        singerService.saves(singerResources.getSingers());
        LOGGER.debug("init import 'init-singer.json' end....");

        singerDoc = new SingerDoc();
        singerDoc.setId(123L);
        singerDoc.setName("测试保存");
        singerDoc.setDesc("描述");
        singerDoc.setBirthday(new Date());
    }

    @Test
    public void saveAndGet(){
        SingerDoc save = singerService.save(singerDoc);
        LOGGER.debug("save() return: {}", JSON.toJSON(save));

        List<SingerDoc> search = singerService.search(0, 10, "测试");
        LOGGER.debug("searchSinger() return: {}", JSON.toJSON(search));

    }

    @Test
    public void save(){
        SingerDoc singer = singerRepository.save(singerDoc);
        System.out.println(JSON.toJSONString(singer));
    }
}
