package com.marry.mhttp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author mal
 * @date 2022-03-04 17:50
 */
@ConfigurationProperties(prefix = "jicklin")
public class HelloProperties {

    public String name;

    public String msg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
