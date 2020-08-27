package com.ffzs.webflux.system_app.controller;

import com.ffzs.webflux.system_app.model.SysApi;
import com.ffzs.webflux.system_app.model.SysHttpResponse;
import com.ffzs.webflux.system_app.service.SysApiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午5:21
 */
@RestController
@RequestMapping("/api/url")
@AllArgsConstructor
public class SysApiController {

    private final SysApiService sysApiService;

    @PostMapping
    public Mono<SysHttpResponse> save (@RequestBody SysApi api) {
        return sysApiService.save(api)
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @PutMapping
    public Mono<SysHttpResponse> update (@RequestBody SysApi api) {
        return sysApiService.save(api)
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @GetMapping("all")
    public Mono<SysHttpResponse> findAll () {
        return sysApiService.findAll()
                .collectList()
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @GetMapping
    public Mono<SysHttpResponse> findByUrl (@RequestParam("url") String url) {
        return sysApiService.findByUrl(url)
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @DeleteMapping
    public Mono<Void> delete (Long id) {
        return sysApiService.delete(id);
    }
}
