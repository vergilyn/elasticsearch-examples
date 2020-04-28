package com.vergilyn.examples.autoconfigure;

import org.elasticsearch.client.RestClientBuilder;

/**
 * @author vergilyn
 * @date 2020-04-24
 * @see org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientBuilderCustomizer
 */
@FunctionalInterface
public interface RestClientBuilderCustomizer {

    /**
     * Customize the {@link RestClientBuilder}.
     * @param builder the builder to customize
     */
    void customize(RestClientBuilder builder);
}
