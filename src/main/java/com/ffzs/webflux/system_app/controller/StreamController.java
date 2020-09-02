package com.ffzs.webflux.system_app.controller;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Locale;

/**
 * @author: ffzs
 * @Date: 2020/9/2 下午3:30
 */
@RestController
@RequestMapping("/api/stream")
@AllArgsConstructor
public class StreamController {

    private final Faker f = new Faker(Locale.CHINA);

    @GetMapping(value = "flux", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    Flux<Long> flux () {
        return Flux.interval(Duration.ofSeconds(1))
                .take(5)
                .map(i-> f.random().nextLong(100));
    }
}
