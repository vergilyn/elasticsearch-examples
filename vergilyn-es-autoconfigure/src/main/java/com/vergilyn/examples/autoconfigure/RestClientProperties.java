package com.vergilyn.examples.autoconfigure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author VergiLyn
 * @date 2019-06-05
 * @see org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientProperties
 */
@ConfigurationProperties(prefix = "vergilyn.es")
public class RestClientProperties {
    private final ClientConfig client = new ClientConfig();
    private final HttpClientConfigCallback http = new HttpClientConfigCallback();
    private final RequestConfigCallback request = new RequestConfigCallback();

    /**
     * Comma-separated list of the Elasticsearch instances to use.
     */
    private List<String> uris = Lists.newArrayList();

    /**
     * Credentials username.
     */
    private String username;

    /**
     * Credentials password.
     */
    private String password;

    public ClientConfig getClientConfig() {
        return client;
    }

    public HttpClientConfigCallback getHttpClientConfig() {
        return http;
    }

    public RequestConfigCallback getRequestConfig() {
        return request;
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class ClientConfig{
        private String clusterName = "elasticsearch";

        private List<String> clusterHosts = new ArrayList<String>(Collections.singletonList("localhost:9200"));

        private int maxRetryTimeout = 30000;

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public List<String> getClusterHosts() {
            return clusterHosts;
        }

        public void setClusterHosts(List<String> clusterHosts) {
            this.clusterHosts = clusterHosts;
        }

        public int getMaxRetryTimeout() {
            return maxRetryTimeout;
        }

        public void setMaxRetryTimeout(int maxRetryTimeout) {
            this.maxRetryTimeout = maxRetryTimeout;
        }

        public HttpHost[] getHosts(){
            HttpHost[] httpHosts = new HttpHost[clusterHosts.size()];

            for (int i = 0, len = clusterHosts.size(); i < len; i++) {
                String[] temp = StringUtils.split(clusterHosts.get(i), ":");
                httpHosts[i] = new HttpHost(temp[0], Integer.valueOf(temp[1]));
            }
            return httpHosts;
        }
    }

    @Setter
    @Getter
    public static class RequestConfigCallback{
        private Duration connectionRequestTimeout = Duration.ofSeconds(1);
        private Duration connectTimeout = Duration.ofSeconds(1);
        private Duration socketTimeout = Duration.ofSeconds(30);
        private int maxRedirects = 50;
    }

    @Setter
    @Getter
    public static class HttpClientConfigCallback{
        private int maxConnTotal = 30;
        private int maxConnPerRoute = 10;
    }
}
