package com.vergilyn.examples.es.repository;

import lombok.Getter;
import lombok.Setter;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/6/23
 */
@Setter
@Getter
public abstract class AbstractDocument {
    private String metaId;
    private String metaIndex;
    private String metaType;
    private Long metaVersion;
    private String metaRouting;

    public AbstractDocument() {
    }

    public AbstractDocument(String id) {
        this.metaId = id;
    }
}
