# Greenis
> 基于 Netty 的 Redis 客户端

...

## 连接上 Redis 服务器

很简单

```java
Bootstrap b = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port) // 设置好 addr 就能连接上
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // ch.pipeline().addLast();
                    }
                });
```

...

## 发送 Redis 命令

[参考 Redis 官方文档](https://redis.io/docs/reference/protocol-spec/)

> To communicate with the Redis server, Redis clients use a protocol called REdis Serialization Protocol (RESP).
> While the protocol was designed specifically for Redis, you can use it for other client-server software projects.

发送的消息格式：
* A client sends a request to the Redis server as an array of strings
* The array's contents are the command and its arguments that the server should execute.

消息序列化要求：
* RESP can serialize different data types including integers, strings, and arrays
* It also features an error-specific type.

Redis 返回消息格式：
* The server's reply type is command-specific 返回的消息类型和发送的命令相关.
* The first byte in an RESP-serialized payload always identifies its type. Subsequent bytes constitute the type's contents.

RESP 的特点：
* RESP is binary-safe and uses prefixed length to transfer bulk data
* it does not require processing bulk data transferred from one process to another