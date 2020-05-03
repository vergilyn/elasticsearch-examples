package com.vergilyn.examples.es.data;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * <a href="https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-testing">spring-boot 2.x testing</a>
 * @author vergilyn
 * @date 2020-05-02
 */
@SpringBootTest(classes = SpringDataEsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = "spring.profiles.active=es")
public abstract class AbstractEsSpringBootTest {

}
