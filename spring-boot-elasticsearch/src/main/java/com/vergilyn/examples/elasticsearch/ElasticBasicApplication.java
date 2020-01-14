package com.vergilyn.examples.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * @author VergiLyn
 */
@SpringBootApplication
@Slf4j
public class ElasticBasicApplication implements CommandLineRunner {
    @Autowired
    ElasticsearchTemplate template;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ElasticBasicApplication.class);
        app.setAdditionalProfiles("elastic");
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
