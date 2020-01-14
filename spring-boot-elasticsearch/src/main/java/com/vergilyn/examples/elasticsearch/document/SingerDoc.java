package com.vergilyn.examples.elasticsearch.document;

import java.util.Date;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * ElasticSearch Mapping: https://www.elastic.co/guide/cn/elasticsearch/guide/current/mapping-intro.html; <BR/>
 * <p>
 *  index 属性控制怎样索引字符串。它可以是下面三个值:<BR/>
 *      analyzed: 以指定的analyzer分析, 并创建索引; 能被搜索. <BR/>
 *      not_analyzed: 创建精确值的索引; 能被搜索. <BR/>
 *      no: 不索引这个域。这个域不会被搜索到。<BR/>
 * </p>
 * <p>
 *  analyzer: 对于analyzed字符串域, 用analyzer属性指定在搜索和索引时使用的分析器.<BR/>
 *      默认, Elasticsearch使用standard分析器,但你可以指定一个内置的分析器替代它.
 *    <pre>
 *        ik_max_word 和 ik_smart 什么区别?
 *        ik_max_word: 会将文本做最细粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌”，会穷尽各种可能的组合；
 *        ik_smart: 会做最粗粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,国歌”。
 *    </pre>
 * </p>
 * <p>
 *     查询映射: localhost:9200/{indexName}/_mapping <BR/>
 *     映射更新: 映射更新存在问题, 一般是创建新的index, 然后创建新的mapping结构
 * </p>
 * <p>
 *   参考: http://www.tianshouzhi.com/api/tutorials/elasticsearch/159
 * </p>
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/14
 */
@Document(indexName = "singers_index"   // 索引库的名称，建议以项目的名称命名
        , type = "singers"               // 类型，建议以实体的名称命名
        , shards = 5                    // 默认分区数
        , replicas = 1                  // 每个分区默认的备份数
        , refreshInterval = "10s"       // 刷新间隔
        , indexStoreType = "fs"         // 索引文件存储类型
)
@Data
@ToString
public class SingerDoc {
    private Long id;

    @Field(type = FieldType.Text
            , analyzer = "standard"  // default: standard [whitespace]
            , searchAnalyzer = "whitespace"
            , includeInParent = false // 没理解, 是include_in_all吗? 但_all默认关闭, 有没看到有属性设置.(include_in_all: true, 该field加入_all; false, 该field不加入_all)
            )
    private String name;

    /** 国籍 */
    @Field(type = FieldType.Text
            , analyzer = "ik_max_word"
            , searchAnalyzer = "ik_max_word")
    private String nationality;

    /** 经济公司 */
    @Field(type = FieldType.Text)
    private String talentAgency;

    /** 代表作品 */
    private String represent;
    @Field(type = FieldType.Date
            , format = DateFormat.year_month_day
    )
    private Date birthday;


    @Field(type = FieldType.Text, index = true
            , analyzer = "ik_max_word"      // 指定字段建立索引时指定的分词器
            , searchAnalyzer = "ik_smart"   // 指定字段搜索时使用的分词器
            , ignoreFields = {}             // 如果某个字段需要被忽略
            , store = false                 // 默认情况下不存储原文

        )
    private String desc;
}
