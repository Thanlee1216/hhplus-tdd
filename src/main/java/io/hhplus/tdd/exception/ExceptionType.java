package io.hhplus.tdd.exception;

import java.util.Arrays;

public enum ExceptionType {
    NOT_EXIST_USER("존재하지 않는 유저입니다.")
    , MINUS_VALUE("0포인트 이상의 값을 넣어주세요.")
    , Exception("에러가 발생했습니다.")
    ;

    private final String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessage(String exceptionType) {
        return Arrays.stream(ExceptionType.values())
                .filter(data -> data.name().equals(exceptionType))
                .findAny()
                .orElse(Exception)
                .getMessage();
    }
}
