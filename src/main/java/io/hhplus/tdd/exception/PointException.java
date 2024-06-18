package io.hhplus.tdd.exception;

public class PointException extends RuntimeException {

    String code;

    public PointException(String code) {
        this.code = code;
    }
    public String getCode() {
        return this.code;
    }
}
