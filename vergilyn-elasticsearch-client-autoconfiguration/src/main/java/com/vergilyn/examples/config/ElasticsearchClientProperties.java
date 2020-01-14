package com.vergilyn.examples.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author VergiLyn
 * @date 2019-06-05
 */
@ConfigurationProperties(prefix = "vergilyn.elastic")
public class ElasticsearchClientProperties {
    private final ClientConfig client = new ClientConfig();

    private final HttpClientConfigCallback http = new HttpClientConfigCallback();

    private final RequestConfigCallback request = new RequestConfigCallback();

    public ClientConfig getClient() {
        return client;
    }

    public HttpClientConfigCallback getHttp() {
        return http;
    }

    public RequestConfigCallback getRequest() {
        return request;
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

    public static class RequestConfigCallback{
        private boolean staleConnectionCheckEnabled = false;
        private boolean redirectsEnabled = true;
        private int maxRedirects = 50;
        private boolean relativeRedirectsAllowed = true;
        private boolean authenticationEnabled = true;
        private int connectionRequestTimeout = -1;
        private int connectTimeout = -1;
        private int socketTimeout = -1;
        private boolean contentCompressionEnabled = true;

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public int getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }
    }

    public static class HttpClientConfigCallback{
        private int maxConnTotal = 30;

        private int maxConnPerRoute = 10;

        public int getMaxConnTotal() {
            return maxConnTotal;
        }

        public void setMaxConnTotal(int maxConnTotal) {
            this.maxConnTotal = maxConnTotal;
        }

        public int getMaxConnPerRoute() {
            return maxConnPerRoute;
        }

        public void setMaxConnPerRoute(int maxConnPerRoute) {
            this.maxConnPerRoute = maxConnPerRoute;
        }
    }
}
