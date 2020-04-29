package com.vergilyn.examples.es.document;

import com.vergilyn.examples.es.repository.AbstractDocument;
import com.vergilyn.examples.es.repository.annotation.EsDocument;

@EsDocument(index = "test_index", type = "test_type", alias = "test_index_alias")
public class TestIndexDocument extends AbstractDocument {
    /**
     * es：text，ik_smart
     */
    private String descSmart;

    /**
     * es：text，ik_max_word
     */
    private String descMax;

    public TestIndexDocument() {
        super();
    }

    public TestIndexDocument(String id, String descSmart, String descMax) {
        super(id);
        this.descSmart = descSmart;
        this.descMax = descMax;
    }

    public String getDescSmart() {
        return descSmart;
    }

    public void setDescSmart(String descSmart) {
        this.descSmart = descSmart;
    }

    public String getDescMax() {
        return descMax;
    }

    public void setDescMax(String descMax) {
        this.descMax = descMax;
    }

}
