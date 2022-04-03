package com.marry.mhttp.service;

import com.marry.mhttp.config.HelloProperties;

/**
 * @author mal
 * @date 2022-03-04 17:52
 */
public class HelloService {

    private HelloProperties helloProperties;
    public HelloService(HelloProperties helloProperties) {
        this.helloProperties = helloProperties;
    }

    public String sayHello() {
        return "hello:" + helloProperties.getName();
    }
}
