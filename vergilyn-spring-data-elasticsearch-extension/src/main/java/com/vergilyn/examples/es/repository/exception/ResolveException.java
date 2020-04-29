package com.vergilyn.examples.es.repository.exception;

/**
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/6/23
 */
public class ResolveException extends RuntimeException {
    public ResolveException(String message) {
        super(message);
    }

    public ResolveException(String message, Throwable cause) {
        super(message, cause);
    }
}
