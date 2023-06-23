package com.demo.rpc.response;

/**
 * RPCResponse
 *
 * @author gnl
 * @since 2023/5/26
 */
public class RPCResponse {
    private Object result;
    private Exception ex;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }
}
