package com.vergilyn.examples.config;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 这么封装不一定好，特别注意：因为是单例，千万别调用close；
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/6/12
 */
@Configuration
@EnableConfigurationProperties(ElasticsearchClientProperties.class)
@Slf4j
public class ElasticRestClientAutoConfiguration implements DisposableBean {

    private final ElasticsearchClientProperties config;
    private RestClientBuilder builder;
    private RestClient restClient;
    private RestHighLevelClient restHighLevelClient;

    public ElasticRestClientAutoConfiguration(ElasticsearchClientProperties config) {
        this.config = config;

        builder = RestClient.builder(config.getClient().getHosts());
        builder.setMaxRetryTimeoutMillis(config.getClient().getMaxRetryTimeout());

        // 配置连接时间延时
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(config.getRequest().getConnectTimeout());
            requestConfigBuilder.setSocketTimeout(config.getRequest().getSocketTimeout());
            requestConfigBuilder.setConnectionRequestTimeout(config.getRequest().getConnectionRequestTimeout());
            return requestConfigBuilder;
        });

        // 使用异步httpclient时设置并发连接数
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(config.getHttp().getMaxConnTotal());
            httpClientBuilder.setMaxConnPerRoute(config.getHttp().getMaxConnPerRoute());
            // TODO 加密通信 or 身份认证
            httpClientBuilder.setSSLContext(null);
            httpClientBuilder.setDefaultCredentialsProvider(null);
            return httpClientBuilder;
        });
        restClient = builder.build();
        restHighLevelClient = new RestHighLevelClient(builder);

        log.info(">>>>>>>>>>> ElasticRestClient init finish <<<<<<<<<<<<");

    }

    @Bean
    public RestClient restClient(){
        return restClient;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return restHighLevelClient;
    }

    @Override
    public void destroy() throws Exception {
        try {
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ElasticRestClient close success!");
        } catch (IOException e) {
            log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ElasticRestClient close failure!", e);
        }
    }
}
