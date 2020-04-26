package com.vergilyn.examples.es.security;

import java.io.IOException;

import com.vergilyn.examples.es.AbstractEsApiTestng;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;

/**
 * @author vergilyn
 * @date 2020-04-26
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.6/java-rest-high-security-authenticate.html">java-rest-high-security-authenticate.html</a>
 */
@Slf4j
public class AuthenticateSecurityTestng extends AbstractEsApiTestng {

    @Test
    public void basicAuthenticationTest() {
        try (final RestHighLevelClient client = new RestHighLevelClient(RestClient
                .builder(new HttpHost("localhost", 9200, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    final BasicCredentialsProvider provider = new BasicCredentialsProvider();
                    provider.setCredentials(
                            AuthScope.ANY,
                            new UsernamePasswordCredentials(
                                    "username",
                                    "password"
                            )
                    );

                    return httpClientBuilder.setDefaultCredentialsProvider(provider);
                })
        )) {
            // Do something.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
