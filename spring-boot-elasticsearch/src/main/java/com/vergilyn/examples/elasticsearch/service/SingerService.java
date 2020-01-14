package com.vergilyn.examples.elasticsearch.service;

import java.util.List;
import java.util.Map;

import com.vergilyn.examples.elasticsearch.document.SingerDoc;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/14
 */
public interface SingerService {

    /**
     * 新增歌手信息到elastic服务
     */
    Iterable<SingerDoc> saves(List<SingerDoc> singerDocs);
    SingerDoc save(SingerDoc singerDoc);

    /**
     * 根据关键词，function score query 权重分分页查询
     */
    List<SingerDoc> search(Integer pageNumber, Integer pageSize, String searchKeyword);

    List<SingerDoc> allSinger();

    Map singerMapping();

}
