package com.vergilyn.examples.es.cases;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

/**
 * @author vergilyn
 * @date 2020-04-28
 */
@Data
@ToString
public class ArticleDto {
    private Long id;
    /** 详情标题 */
    private String title;
    /** 列表标题 */
    private String listTitle;
    /** 浏览数 */
    private int viewNum;
    /** 发布时间 */
    private LocalDateTime publishTime;
    /** 标签，英文逗号分隔 */
    private String tags;
    /** 摘要，纯文字 */
    private String summary;
    /** 内容，富文本（含html标签）*/
    private String content;
}
