package com.demo.greenis;

import redis.clients.jedis.Jedis;

public class JedisClient {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);
        String value = jedis.get("foo");
        System.out.println(value);
    }
}
