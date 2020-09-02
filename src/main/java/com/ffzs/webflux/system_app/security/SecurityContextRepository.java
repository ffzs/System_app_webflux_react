package com.ffzs.webflux.system_app.security;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/16 下午8:05
 */

//@Component
@AllArgsConstructor
@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager jwtAuthenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @SneakyThrows
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
//        log.info("访问 ServerSecurityContextRepository  。。。。。。。。。。。");

        String token = exchange.getAttribute("token");
        log.info("token {}", token);
        return jwtAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(token, token)
                )
                .map(SecurityContextImpl::new);
    }
}
