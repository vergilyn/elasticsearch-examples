package com.vergilyn.examples.es.repository.exception;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/6/23
 */
public class EsResponseException extends RuntimeException {
    public EsResponseException(String message) {
        super(message);
    }

    public EsResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
