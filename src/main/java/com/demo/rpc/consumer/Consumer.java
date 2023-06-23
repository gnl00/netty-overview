package com.demo.rpc.consumer;

import com.demo.rpc.client.RPCClient;
import com.demo.rpc.constant.Constant;

import java.util.concurrent.atomic.AtomicReference;

/**
 * RPC Consumer
 *
 * @author gnl
 * @since 2023/5/26
 */
public class Consumer {
    public static void main(String[] args) {
        // 需要两个线程，一个启动 client 服务，启动成功监听 client 直到关闭
        // 另一个线程在 client 启动后执行 rpc 请求调用操作
        AtomicReference<RPCClient> rpcClient = new AtomicReference<>();
        Thread starter = new Thread(() -> {
            final RPCClient finalClient = new RPCClient(Constant.HOST, Constant.PORT);
            finalClient.start();
            rpcClient.set(finalClient);
        }, "starter");

        Thread sender = new Thread(() -> {
            rpcClient.get().send();
        }, "sender");


        System.out.println(starter);
        starter.start();
        try {
            starter.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sender.start();
    }
}
