package com.vergilyn.examples;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/4/14
 */
@SpringBootApplication
@Slf4j
public class HighRestClientESApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HighRestClientESApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>> HighRestClientESApplication END!");
    }
}
