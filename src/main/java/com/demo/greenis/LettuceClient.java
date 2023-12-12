package com.demo.greenis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;

public class LettuceClient {
    public static void main(String[] args) {
        RedisClient rc = RedisClient.create("redis://localhost:6379");
        RedisCommands<String, String> cmd = rc.connect().sync();
        String s = cmd.get("foo");
        System.out.println(s);
    }
}
