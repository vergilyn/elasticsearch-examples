package com.vergilyn.examples.es.repository;


import com.vergilyn.examples.es.document.TestIndexDocument;
import com.vergilyn.examples.es.repository.support.AbstractEsCrudRepository;

import org.springframework.stereotype.Repository;

@Repository
public class TestEsCrudRepository extends AbstractEsCrudRepository<TestIndexDocument, String> {


}
