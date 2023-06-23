package com.demo.rpc.provider;

import com.demo.rpc.constant.Constant;
import com.demo.rpc.server.RPCServer;

/**
 * RPC Provider
 *
 * @author gnl
 * @since 2023/5/26
 */
public class Provider {
    public static void main(String[] args) {
        new RPCServer(Constant.PORT).start();
    }
}
