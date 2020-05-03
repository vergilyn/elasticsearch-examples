package com.vergilyn.examples.es.data.autoconfigure;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vergilyn.examples.es.data.document.DynamicIndexDocument;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author vergilyn
 * @date 2020-05-03
 */
@Component("dynamicIndexStrategy")
@Scope("singleton")
public class DynamicIndexStrategy {
    private LocalDate now = LocalDate.now();

    public String indexName(){
        return DynamicIndexDocument.INDEX_PREFIX + now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public LocalDate getNow() {
        return now;
    }

    public void setNow(LocalDate now) {
        this.now = now;
    }

}
