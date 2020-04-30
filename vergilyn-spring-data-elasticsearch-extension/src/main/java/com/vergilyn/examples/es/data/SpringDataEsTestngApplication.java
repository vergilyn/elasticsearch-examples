package com.vergilyn.examples.es.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author vergilyn
 * @date 2020-04-30
 */
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.vergilyn.examples.es.data")
public class SpringDataEsTestngApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringDataEsTestngApplication.class);
        application.setAdditionalProfiles("es");

        application.run(args);
    }
}
