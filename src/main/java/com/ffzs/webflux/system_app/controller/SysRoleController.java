package com.ffzs.webflux.system_app.controller;

import com.ffzs.webflux.system_app.model.SysHttpResponse;
import com.ffzs.webflux.system_app.model.SysRole;
import com.ffzs.webflux.system_app.service.SysRoleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午5:31
 */

@RestController
@RequestMapping("/api/role")
@AllArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @PostMapping
    public Mono<SysHttpResponse> save (@RequestBody SysRole role) {
        return sysRoleService.save(role)
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @PutMapping
    public Mono<SysHttpResponse> update (@RequestBody SysRole role) {
        return sysRoleService.save(role)
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @GetMapping("all")
    public Mono<SysHttpResponse> findAll () {
        return sysRoleService.findAll()
                .collectList()
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @GetMapping
    public Mono<SysHttpResponse> findByUrl (@RequestParam("name") String name) {
        return sysRoleService.findByName(name)
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @DeleteMapping
    public Mono<Void> delete (Long id) {
        return sysRoleService.delete(id);

    }
}
