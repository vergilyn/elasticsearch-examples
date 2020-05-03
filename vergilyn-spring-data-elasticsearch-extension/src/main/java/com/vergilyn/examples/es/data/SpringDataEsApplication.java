package com.vergilyn.examples.es.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * FIXME
 * @author vergilyn
 * @date 2020-04-30
 */
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.vergilyn.examples.es.data")
@Slf4j
public class SpringDataEsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringDataEsApplication.class);
        application.setAdditionalProfiles("es");

        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("SpringDataEsApplication startup!");
    }
}
