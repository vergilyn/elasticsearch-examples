package com.vergilyn.examples.autoconfigure;

import java.time.Duration;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author vergilyn
 * @date 2020-04-24
 * @see org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientConfigurations
 */
public class RestClientConfigurations {
    private static final String ES_ADDRESS = "127.0.0.1:9200";

    @Configuration(proxyBeanMethods = false)
    static class RestClientBuilderConfiguration {

        @Bean
        @ConditionalOnMissingBean
        RestClientBuilder elasticsearchRestClientBuilder(RestClientProperties properties,
                                                         ObjectProvider<RestClientBuilderCustomizer> builderCustomizers) {
            List<String> uris = properties.getUris();
            if (uris.isEmpty()){
                uris.add(ES_ADDRESS);
            }

            HttpHost[] hosts = uris.stream().map(HttpHost::create).toArray(HttpHost[]::new);

            RestClientBuilder builder = RestClient.builder(hosts);

            PropertyMapper map = PropertyMapper.get();

            setHttpClientConfigCallback(properties, map, builder);

            setRequestConfigCallback(properties.getRequestConfig(), map, builder);

            builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
            return builder;
        }

        private static void setHttpClientConfigCallback(RestClientProperties properties, PropertyMapper map, RestClientBuilder builder){
            RestClientProperties.HttpClientConfigCallback httpClientConfig = properties.getHttpClientConfig();
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                map.from(properties::getUsername).whenHasText().as(s -> {
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    Credentials credentials = new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword());
                    credentialsProvider.setCredentials(AuthScope.ANY, credentials);
                    return credentialsProvider;
                }).to(httpClientBuilder::setDefaultCredentialsProvider);

                map.from(httpClientConfig::getMaxConnTotal).whenNonNull()
                        .to(httpClientBuilder::setMaxConnTotal);

                map.from(httpClientConfig::getMaxConnPerRoute).whenNonNull()
                        .to(httpClientBuilder::setMaxConnPerRoute);

                return httpClientBuilder;
            });
        }

        private static void setRequestConfigCallback(RestClientProperties.RequestConfigCallback requestConfig, PropertyMapper map, RestClientBuilder builder){
            builder.setRequestConfigCallback((requestConfigBuilder) -> {
                map.from(requestConfig::getConnectionRequestTimeout).whenNonNull().asInt(Duration::toMillis)
                        .to(requestConfigBuilder::setConnectionRequestTimeout);

                map.from(requestConfig::getSocketTimeout).whenNonNull().asInt(Duration::toMillis)
                        .to(requestConfigBuilder::setSocketTimeout);

                map.from(requestConfig::getConnectTimeout).whenNonNull().asInt(Duration::toMillis)
                        .to(requestConfigBuilder::setConnectTimeout);

                map.from(requestConfig::getMaxRedirects).whenNonNull()
                        .to(requestConfigBuilder::setMaxRedirects);

                return requestConfigBuilder;
            });
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(RestHighLevelClient.class)
    static class RestHighLevelClientConfiguration {

        @Bean
        @ConditionalOnMissingBean
        RestHighLevelClient elasticsearchRestHighLevelClient(RestClientBuilder restClientBuilder) {
            return new RestHighLevelClient(restClientBuilder);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(RestHighLevelClient.class)
        RestClient elasticsearchRestClient(RestClientBuilder builder,
                                           ObjectProvider<RestHighLevelClient> restHighLevelClient) {
            RestHighLevelClient client = restHighLevelClient.getIfUnique();
            if (client != null) {
                return client.getLowLevelClient();
            }
            return builder.build();
        }

    }

    /*@Configuration(proxyBeanMethods = false)
    static class RestClientFallbackConfiguration {

        @Bean
        @ConditionalOnMissingBean
        RestClient elasticsearchRestClient(RestClientBuilder builder) {
            return builder.build();
        }

    }*/
}
