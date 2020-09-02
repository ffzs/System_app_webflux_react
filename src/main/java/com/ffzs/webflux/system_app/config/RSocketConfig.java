package com.ffzs.webflux.system_app.config;

import io.rsocket.RSocketFactory;
import org.springframework.boot.rsocket.server.ServerRSocketFactoryProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RSocketConfig {

    @Bean
    ServerRSocketFactoryProcessor serverRSocketFactoryProcessor() {
        return RSocketFactory.ServerRSocketFactory::resume;
    }
}
