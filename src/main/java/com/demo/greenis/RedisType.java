package com.demo.greenis;

public enum RedisType {
    STRING("+");

    private String firstByte;

    RedisType(String firstByte) {
        this.firstByte = firstByte;
    }
}
