package com.demo.rpc.request;

/**
 * 还有改进空间，可以自定义 RPCRequest 和 RPCResponse 等
 *
 * @author gnl
 * @since 2023/5/26
 */
public class RPCRequest {
    private String service;
    private String method;
    private Object[] args;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
