package com.demo.sticky_split.split;

import java.util.Arrays;

/**
 * MessageProtocol 自定义消息协议
 *
 * @author gnl
 * @since 2023/5/23
 */
public class MessageProtocol {
    private int len;
    private byte[] content;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public MessageProtocol() {
    }

    public MessageProtocol(int len, byte[] content) {
        this.len = len;
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageProtocol{" +
                "len=" + len +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
