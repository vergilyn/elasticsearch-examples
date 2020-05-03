package com.vergilyn.examples.es.data;

import java.util.Optional;

import com.vergilyn.examples.es.data.document.ArticleDocument;
import com.vergilyn.examples.es.data.repository.ArticleRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author vergilyn
 * @date 2020-05-02
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ArticleSpringBootTest extends AbstractEsSpringBootTest {
    private static final Long X_ID = 409839163L;
    private static final ArticleDocument ARTICLE_DOCUMENT = new ArticleDocument().init(X_ID);

    @Autowired
    private ArticleRepository articleRepo;

    @org.junit.jupiter.api.Test
    @Order(1)
    public void save(){
        articleRepo.save(ARTICLE_DOCUMENT);
    }

    @org.junit.jupiter.api.Test
    @Order(2)
    public void findById(){
        Optional<ArticleDocument> document = articleRepo.findById(X_ID);

        Assert.assertNotNull(document.get());
        Assert.assertEquals(ARTICLE_DOCUMENT.getContent(), document.get().getContent());
    }

    @org.junit.jupiter.api.Test
    @Order(3)
    public void deleteById(){
        articleRepo.deleteById(X_ID);

        boolean exists = articleRepo.existsById(X_ID);

        Assert.assertFalse(exists);
    }
}
