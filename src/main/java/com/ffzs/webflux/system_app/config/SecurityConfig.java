package com.ffzs.webflux.system_app.config;


import com.ffzs.webflux.system_app.handler.JwtWebFilter;
import com.ffzs.webflux.system_app.security.SecurityContextRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author: ffzs
 * @Date: 2020/8/11 下午4:22
 */

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final SecurityContextRepository securityRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtWebFilter jwtWebFilter
    ) {

        return http
                .authorizeExchange()
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
//                .pathMatchers("/api/user/**").hasAnyRole("ADMIN", "HR")
                .pathMatchers("/api/user/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAfter(jwtWebFilter, SecurityWebFiltersOrder.FIRST)  // 这里注意执行位置一定要在securityContextRepository
                .securityContextRepository(securityRepository)
                .formLogin().disable()
            .httpBasic().disable()
                .csrf().disable()
                .logout().disable()
                .build();
    }
}