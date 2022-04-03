package com.marry.mhttp.config;

import com.marry.mhttp.service.HelloService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mal
 * @date 2022-03-04 17:54
 */
@Configuration
@ConditionalOnClass(HelloService.class)
@EnableConfigurationProperties(HelloProperties.class)
public class HelloServiceAutoConfiguration {

    @Bean
    HelloService helloService(HelloProperties helloProperties) {
        HelloService helloService = new HelloService(helloProperties);
        return helloService;

    }
}
