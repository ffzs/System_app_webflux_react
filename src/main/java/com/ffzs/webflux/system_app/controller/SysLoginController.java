package com.ffzs.webflux.system_app.controller;

import com.ffzs.webflux.system_app.model.LoginResponse;
import com.ffzs.webflux.system_app.model.SysHttpResponse;
import com.ffzs.webflux.system_app.model.SysUserDetails;
import com.ffzs.webflux.system_app.service.JwtSigner;
import com.ffzs.webflux.system_app.service.RedisService;
import com.ffzs.webflux.system_app.service.SysUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午2:31
 */

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class SysLoginController {
    private final PasswordEncoder password = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final SysUserService sysUserService;
    private final JwtSigner jwtSigner;
    private final RedisService redisService;

    @PostMapping("login")
    public Mono<SysHttpResponse> login (@RequestBody Map<String, String> user) {

        return Mono.justOrEmpty(user.get("username"))
                .flatMap(sysUserService::findByUsername)
                .filter(it -> password.matches(user.get("password"), it.getPassword()))
                .map(it -> {
                            List<String> roles = it.getRoles();
                            String token = jwtSigner.generateToken(SysUserDetails
                                    .builder()
                                    .username(it.getUsername())
                                    .password(user.get("password"))
                                    .authorities(roles)
                                    .build()
                            );
                            redisService.saveToken(token);
                            return SysHttpResponse
                                    .ok("成功登录", LoginResponse.fromUser(it).withToken(token));
                        }
                )
                .onErrorResume(e -> Mono.empty())
                .switchIfEmpty(Mono.just(new SysHttpResponse(HttpStatus.UNAUTHORIZED.value(), "登录失败", null)));
    }

    @GetMapping("logout")
    public Mono<SysHttpResponse> logout (@RequestParam("token") String token) {
        return Mono.just(token)
                .flatMap(redisService::deleteToken)
                .flatMap(it -> Mono.just(SysHttpResponse.ok(it)))
                .onErrorResume(e -> Mono.just(SysHttpResponse.error5xx("删除token出错", e.getMessage())))
                .switchIfEmpty(Mono.just(SysHttpResponse.error5xx("删除token出错", null)));
    }
}
