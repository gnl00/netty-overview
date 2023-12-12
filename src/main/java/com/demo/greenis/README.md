# Greenis

> 基于 Netty 的 Redis 客户端，实现了基本的消息收发。

...

## 连接上 Redis 服务器

跟简单

```java
Bootstrap b = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port) // 设置好 host 和 port
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // ch.pipeline().addLast();
                    }
                });
```

...

---

<br>

## 发送 Redis 命令

[参考 Redis 官方文档](https://redis.io/docs/reference/protocol-spec/)

> To communicate with the Redis server, Redis clients use a protocol called REdis Serialization Protocol (RESP).
> While the protocol was designed specifically for Redis, you can use it for other client-server software projects.

可以根据 [RESP 规范](https://redis.io/docs/reference/protocol-spec/)给 Redis 服务器发送 RESP 命令。

...

### 发送消息规范
* RESP is a binary protocol that uses control sequences encoded in **standard ASCII**.
* The _first byte_ in an RESP-serialized payload always identifies RESP's type. _Subsequent bytes_ constitute the type's contents.
* The \r\n (CRLF) is the protocol's terminator, which always separates its parts.

...

1、发送的消息需要编码成 ASCII 码
2、发送的消息的第一个字节表示 RESP 命令类型，可以是 + 或者 * 或者 $，[参考](https://redis.io/docs/reference/protocol-spec/)
3、\r\n (CRLF) 在 RESP 命令中被用作终止符，用来切分 RESP 命令不同的部分

...

### 发送第一条 RESP 命令


首先使用 redis-cli 开启一个监控窗口，可以查看消息是否送达

```shell
$ redis-cli
127.0.0.1:6379>set foo bar
OK
127.0.0.1:6379> monitor # 输入 MONiTOR 命令开始监控，MONITOR 命令会监控到所有客户端发送给 Redis 服务器的消息。
```

按照 RESP 规范，一条最简单的 String 命令应该如下：
    
```shell
+\r\ninfo\r\n
```

首先 `+` 表示该命令是一条简单的 String 命令，第一个 `\r\n` 将命令分割成两部分，`info` 是命令的内容，最后一个 `\r\n` 表示命令结束。
接下来尝试将它发送到 Redis 服务器。

```java
    ChannelFuture future = b.connect().sync();

    String cmdStr = "+\r\ninfo\r\n";
    ByteBuf buffer = Unpooled.buffer(cmdStr.length());
    buffer.writeBytes(cmdStr.getBytes(StandardCharsets.US_ASCII));
    
    future.channel().writeAndFlush(buffer);
```
观察 monitor

```shell
127.0.0.1:6379> monitor
OK
1702359898.890509 [0 172.17.0.1:38406] "info"
```

可以看到，Redis 服务器成功接收到了来自客户端的消息。

...

---

<br>

### 发送第二条 RESP 命令

我们意见发送了一条简单的 String 命令，接下来尝试发送一条带参数的 String 命令。

发送带参数的 String 命令，需要使用 `*` 或者 `$` 作为命令的第一个字节，表示我们发送的是一个批量的 String 或者是一个数组。一条 `get fooo` 命令应该被序列化成下面的样子：

```shell
*2\r\n$3\r\nget\r\n$4\r\nfooo\r\n
```

`*2` 表示这一组命令长度为 2；`$3` 表示接下来的元素长度为 3 也就是 `get` 字符串的长度；`$4` 表示 `fooo` 字符串的长度

发送至服务器

```java
    ChannelFuture future = b.connect().sync();

    String cmdStr = "*2\r\n$3\r\nget\r\n$4\r\nfooo\r\n";
    ByteBuf buffer = Unpooled.buffer(cmdStr.length());
    buffer.writeBytes(cmdStr.getBytes(StandardCharsets.US_ASCII));
    
    future.channel().writeAndFlush(buffer);
```

观察 monitor

```shell
1702360529.367891 [0 172.17.0.1:49774] "get" "fooo"
```

发送成功。

...

---

<br>

## 接收 Redis 响应

理解了消息的发送之后，解析响应就是对发送消息的逆向思维。

```java
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("inbound: " + msg);
        String recvStr = msg.toString(StandardCharsets.US_ASCII);


        String firstByte = recvStr.substring(0, 1); // 从第一个 byte 判断返回类型，来决定使用什么容器来接收数据
        // handle first byte...
        // 这里偷懒了...

        String subResponse = recvStr.substring(1); // 跳过 first byte 处理内容
        String[] responseArray = subResponse.split(Symbol.CRLF);
        Integer totalLen = Integer.valueOf(responseArray[0]);

        ArrayList<String> responseList = new ArrayList<>(totalLen);
        for (int i = 1; i < responseArray.length; i++) {
            String curr = responseArray[i];
            // check symbol
            if (!curr.startsWith(Symbol.DOLLAR)) {
                responseList.add(curr);
            }
        }

        if (responseList.size() > 1) {
            System.out.println(responseList);
        }
        System.out.println(responseList.get(0));
    }
```

响应消息的格式和发送的消息是一样的，只需要逆向解析即可。上面这段代码实现了简单接收，但是还有很多问题，比如说：
* 应该从 first byte 判断返回的消息类型，可能是 String/Array/Error。这里只做了简单的 String/Array 处理。
* 将结果添加到 ArrayList 的时候应该根据 $ 符号后面的长度判断下一个元素长度是否一致，避免出现不合规的响应。这里偷懒处理了。
* ...

...

## 扩展

1、可以参考 `redis.clients.jedis.Jedis#set` 和 `redis.clients.jedis.Connection#executeCommand` 看第三方库如何完善 RESP 协议的实现。
2、io.lettuce.core.RedisClient#create

---

<br>

## 参考
* https://redis.io/docs/reference/protocol-spec/