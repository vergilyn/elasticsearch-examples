package com.vergilyn.examples.elasticsearch.constant;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.vergilyn.examples.elasticsearch.document.SingerDoc;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * FIXME: 不清楚更好的初始化数据的方法, 不管是json还是yml文件.
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/14
 */
@Configuration
@Component
public class SingerResources {

    @Value("classpath:init-data/init-singer.json")
    private Resource resource;

    public List<SingerDoc> getSingers() {
        try {
            String json = FileUtils.readFileToString(resource.getFile(), "UTF-8");
            List<SingerDoc> singerDocs = JSON.parseArray(json, SingerDoc.class);
            return singerDocs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
