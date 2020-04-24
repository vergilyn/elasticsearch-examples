package com.vergilyn.examples.autoconfigure;

import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author vergilyn
 * @date 2020-04-24
 * @see org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RestClient.class)
@EnableConfigurationProperties(RestClientProperties.class)
@Import({ RestClientConfigurations.RestClientBuilderConfiguration.class,
        RestClientConfigurations.RestHighLevelClientConfiguration.class})
public class RestClientAutoConfiguration {

}
