package com.ffzs.webflux.system_app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;



@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class}) // 使用jwt，不再使用UserDetails
public class SystemAppApplication {

    public static void main(String[] args) {

        SpringApplication.run(SystemAppApplication.class, args);
    }
}


