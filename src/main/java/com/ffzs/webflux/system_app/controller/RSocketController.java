package com.ffzs.webflux.system_app.controller;

import com.ffzs.webflux.system_app.model.Weather;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * @author: ffzs
 * @Date: 2020/9/2 下午4:35
 */

@Controller
@Slf4j
public class RSocketController {
    private final Faker f = new Faker(Locale.CHINA);


    @MessageMapping("weather")
    public Flux<Weather> stream(final String request) {

        return Flux
                .interval(Duration.ofSeconds(1))
                .filter(it -> it%10!=3)
                .map(index -> Weather.builder()
                        .id(index)
                        .date(LocalDateTime.now().minusDays(1000-index))
                        .direction(f.random().nextLong(360))
                        .speed(f.random().nextLong(150))
                        .temperature(f.random().nextLong(15)+20)
                        .build()
                )
                .log();
    }
}
