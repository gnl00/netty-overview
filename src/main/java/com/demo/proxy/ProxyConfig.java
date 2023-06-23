package com.demo.proxy;

public class ProxyConfig {
    private final String host;
    private final int port;
    private final int mappingPort;

    public ProxyConfig(String host, int port, int mappingPort) {

        // parse external configuration
        // ...
        this.host = host;
        this.port = port;
        this.mappingPort = mappingPort;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getMappingPort() {
        return mappingPort;
    }
}
