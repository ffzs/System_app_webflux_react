package com.ffzs.webflux.system_app.service;

import com.ffzs.webflux.system_app.model.SysUser;
import com.ffzs.webflux.system_app.repository.SysUserRepository;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * @author: ffzs
 * @Date: 2020/8/30 下午12:53
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserDataFaker {

    private final SysUserService sysUserService;
    private final SysUserRepository sysUserRepository;
    private final Faker f = new Faker(Locale.CHINA);
    private final String[] roles = { "it", "op", "hr", "user", "pm"};

    private SysUser fakeUser (String avatar) {
        String name = f.name().fullName();
        return SysUser.builder()
                .avatar(avatar)
                .username(name)
                .password("123zxc")
                .email(f.internet().safeEmailAddress(name))
                .mobile(f.phoneNumber().cellPhone())
                .frozen(f.random().nextInt(1,10) < 9? 0:1)
                .roles(List.of("ROLE_" + roles[f.random().nextInt(0,roles.length-1)].toUpperCase()))
                .build();
    }


    public Flux<SysUser> fakeUserData (Long count) throws IOException {

        return Flux.fromStream(Files.lines(Paths.get("avatar.txt")).limit(count))
                .onErrorContinue((e, i) -> log.error("{} {}", i, e.getMessage()))
                .map(this::fakeUser)
                .flatMap(user -> sysUserRepository
                        .existsByUsername(user.getUsername())
                        .filter(it->it)
                        .then(Mono.just(user)))
                .flatMap(sysUserService::save);
    }
}
