package com.vergilyn.examples.es.data.document;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;
import org.elasticsearch.index.VersionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 *
 * <a href="es-usage-examples/src/test/resources/vergilyn-article_index_source.json">vergilyn-article_index_source.json</a>
 * @author vergilyn
 * @date 2020-04-30
 */
@TypeAlias(ArticleDocument.ES_INDEX_ALIAS)
@Setting(settingPath = "spring-data-elasticsearch_settings.json")
@Document(indexName = ArticleDocument.ES_INDEX,
        // shards = 3, replicas = 1, refreshInterval = "10s", indexStoreType = "fs",  // settings 配置
        createIndex = true, versionType = VersionType.EXTERNAL)
@Data
public class ArticleDocument {
    public static final String ES_INDEX = "spring-data-elasticsearch";
    public static final String ES_INDEX_ALIAS = ES_INDEX + "_alias";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final byte STATUS_DELETE = Byte.MIN_VALUE;
    public static final byte STATUS_DRAFT = 0;
    public static final byte STATUS_PUBLISH = Byte.MAX_VALUE;

    @Id  // Applied at the field level to mark the field used for identity purpose.
    @Field(name = "id", type = FieldType.Keyword)
    private Long id;

    /** -128: 删除, 0: 草稿, 127: 发布*/
    @Field(name = "status", type = FieldType.Byte)
    private Byte status;

    /** 详情标题 TODO 2020-04-30 title.keyword */
    @Field(name = "title", type = FieldType.Text,
            analyzer = "ik_max_word", searchAnalyzer = "ik_smart_synonym")
    private String title;

    /** 列表标题 TODO 2020-04-30 listTitle.keyword */
    @Field(name = "list_title", type = FieldType.Text,
            analyzer = "ik_max_word", searchAnalyzer = "ik_smart_synonym")
    private String listTitle;

    /** 浏览数 */
    @Field(name = "view_num", type = FieldType.Integer)
    private int viewNum;

    /** 发布时间 */
    @Field(name = "publish_time", type = FieldType.Date,
            format = DateFormat.custom, pattern = DATETIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime publishTime;

    /** 标签，英文逗号分隔 */
    @Field(name = "tags", type = FieldType.Text,
            analyzer = "comma_analyzer", searchAnalyzer = "ik_smart")
    private String tags;

    /** 摘要，纯文字 */
    @Field(name = "summary", type = FieldType.Text,
            analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String summary;

    /** 内容，富文本（含html标签）*/
    @Field(name = "content", type = FieldType.Text,
            analyzer = "ik_smart_html", searchAnalyzer = "ik_smart_html")
    private String content;

    public ArticleDocument init(Long id){
        this.setId(id);
        this.setStatus(ArticleDocument.STATUS_DRAFT);
        this.setTitle("详情标题");
        this.setListTitle("列表标题");
        this.setViewNum(10);
        this.setPublishTime(LocalDateTime.now());
        this.setTags("java,elasticsearch");
        this.setSummary("摘要");
        this.setContent("<p>正文内容</p>");

        return this;
    }
}
