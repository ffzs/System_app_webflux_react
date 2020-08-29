package com.ffzs.webflux.system_app.controller;

import com.ffzs.webflux.system_app.model.SysHttpResponse;
import com.ffzs.webflux.system_app.model.SysUser;
import com.ffzs.webflux.system_app.service.SysUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


/**
 * @author: ffzs
 * @Date: 2020/8/26 下午4:15
 */

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
@Slf4j
@CrossOrigin
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping
    Mono<SysHttpResponse> findByName (@RequestParam("username") String username) {
        return sysUserService.findByUsername(username)
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @GetMapping("all")
    Mono<SysHttpResponse> findAll () {
        return sysUserService.findAll()
                .collectList()
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'IT')")
    Mono<SysHttpResponse> save (@RequestBody SysUser user) {
        log.info("user {}", user);
        return sysUserService.save(user)
                .map(it->SysHttpResponse
                        .builder()
                        .status(HttpStatus.OK.value())
                        .message("存储成功")
                        .data(it.withPassword(null))
                        .build()
                )
                .onErrorResume(err -> {
                    SysHttpResponse response = new SysHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "运行出错", err.getMessage());
                    if (err instanceof DataIntegrityViolationException) {
                        response = new SysHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "存储数据某些字段不唯一", err.getMessage());
                    }
                    return Mono.just(response);
                });
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'IT')")
    Mono<SysHttpResponse> update (@RequestBody SysUser user) {
        return sysUserService.save(user)
                .map(SysHttpResponse::ok)
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx(e.getMessage(), e)));
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    Mono<Void> deleteById (@RequestParam("id") long id) {
        return sysUserService.deleteById(id);
    }

}
