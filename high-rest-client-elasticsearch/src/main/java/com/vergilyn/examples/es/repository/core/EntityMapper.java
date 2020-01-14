package com.vergilyn.examples.es.repository.core;

import java.io.IOException;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/7/5
 * @see org.springframework.data.elasticsearch.core.EntityMapper
 */
public interface EntityMapper {

    public String mapToString(Object object) throws IOException;

    public <T> T mapToObject(String source, Class<T> clazz) throws IOException;
}
