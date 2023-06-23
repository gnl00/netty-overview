package com.demo.rpc.provider;

import com.demo.rpc.api.RPCService;

/**
 * RPCServiceImpl
 *
 * @author gnl
 * @since 2023/5/26
 */
public class RPCServiceImpl implements RPCService {
    @Override
    public String invoke(String param) {
        return "RPCService#invoke ==> " + param;
    }
}
