package com.vergilyn.examples.elasticsearch.document;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "test_city", type = "city", createIndex = false)
@Data
@ToString
public class CityDoc implements Serializable {

    /** 城市编号 */
    private Long id;

    /** 城市名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 城市评分 */
    private Integer score;

    public CityDoc(Long id, String name, String description, Integer score) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.score = score;
    }

}
