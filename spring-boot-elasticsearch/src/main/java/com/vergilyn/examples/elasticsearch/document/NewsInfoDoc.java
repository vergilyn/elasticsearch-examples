package com.vergilyn.examples.elasticsearch.document;

import java.util.Date;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/1/17
 */
@Document(indexName = "news_info_index"   // 索引库的名称，建议以项目的名称命名
        , type = "news_info_type"         // 类型，建议以实体的名称命名
        , shards = 5                    // 默认分区数
        , replicas = 1                  // 每个分区默认的备份数
        , refreshInterval = "10s"       // 刷新间隔
        , indexStoreType = "fs"         // 索引文件存储类型
)
// @Setting
// @Mapping
@Data
@ToString
public class NewsInfoDoc {
    /**
     * infoClassifyId
     */
    @Field(type = FieldType.Long, index = false)
    private Long id;

    @Field(type = FieldType.Long, index = false)
    private Long infoId;

    @Field(type = FieldType.Long, index = false)
    private Long appId;

    @Field(type = FieldType.Date, index = false)
    private Date onlineTime;

    @Field(type = FieldType.Long, index = true)
    private Long dataStatus;

    @Field(type = FieldType.Text, index = true, analyzer = "ik_smart")
    private String listTitle;

    @Field(type = FieldType.Text, index = true, analyzer = "ik_smart")
    private String infoKeywords;

    @Field(type = FieldType.Text, index = true, analyzer = "ik_smart")
    private String infoTitle;

    @Field(type = FieldType.Text, index = true, analyzer = "ik_smart")
    private String infoContent;

}
