package com.ffzs.webflux.system_app.service;

import com.ffzs.webflux.system_app.model.SysRole;
import com.ffzs.webflux.system_app.model.SysUser;
import com.ffzs.webflux.system_app.model.SysUserRole;
import com.ffzs.webflux.system_app.repository.SysRoleRepository;
import com.ffzs.webflux.system_app.repository.SysUserRepository;
import com.ffzs.webflux.system_app.repository.SysUserRoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author: ffzs
 * @Date: 2020/8/26 下午1:07
 */

@Service
@AllArgsConstructor
@Slf4j
public class SysUserService {

    private final PasswordEncoder password = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final SysUserRepository sysUserRepository;
    private final SysUserRoleRepository sysUserRoleRepository;
    private final SysRoleRepository sysRoleRepository;
    private final MarkDataService mark;


    private Mono<SysUser> addRoles (SysUser user) {

        return sysUserRoleRepository
                .findByUserId(user.getId())
                .map(SysUserRole::getRoleId)
                .flatMap(sysRoleRepository::findById)
                .map(SysRole::getName)
                .collectList()
                .map(it -> {
                    user.setRoles(it);
                    return user;
                });
    }

    public Flux<SysUser> findAll () {
        return sysUserRepository.findAll()
                .flatMap(this::addRoles);
    }

    public Mono<SysUser> findByUsername (String username) {
        return sysUserRepository.findByUsername(username)
                .flatMap(this::addRoles);
    }


    private Mono<SysUser> saveRoles (SysUser user) {
        List<String> roles = user.getRoles();
        if (roles==null || roles.isEmpty()) return Mono.just(user);
        return Flux.fromIterable(roles)
                .flatMap(role -> sysRoleRepository.findByName(role)
                        .switchIfEmpty(
                                mark.createObj(new SysRole(role))
                                        .flatMap(sysRoleRepository::save)
                                        .map(SysRole::getName)
                                        .flatMap(sysRoleRepository::findByName)
                        )
                        .map(SysRole::getId)

                )
                .flatMap(roleId -> sysUserRoleRepository
                        .findByUserIdAndRoleId(user.getId(), roleId)
                        .switchIfEmpty(mark.createObj(new SysUserRole(user.getId(), roleId))
                                .cast(SysUserRole.class)
                                .flatMap(sysUserRoleRepository::save)
                        )
                        .map(SysUserRole::getId))
                .collectList()
                .then(Mono.just(user));
    }


    public Mono<SysUser> save (SysUser user) {
        user.setPassword(password.encode(user.getPassword()));

        if (user.getId() != 0) {  // id不为0为更新update
            return mark.updateObj(user)
                    .flatMap(it -> sysUserRepository
                            .findByUsername(user.getUsername())
                            .map(oldUser -> it
                                    .withCreateBy(oldUser.getCreateBy())
                                    .withCreateTime(oldUser.getCreateTime())
                            ))
                    .flatMap(sysUserRepository::save)
                    .flatMap(this::saveRoles);
        }
        else {   // id为0为create
            return mark.createObj(user)
                    .flatMap(sysUserRepository::save)
                    .map(SysUser::getUsername)
                    .flatMap(sysUserRepository::findByUsername)
                    .map(it -> it.withRoles(user.getRoles()))
                    .flatMap(this::saveRoles);
        }
    }


    public Mono<Void> deleteById (Long id) {
        return sysUserRepository.deleteById(id)
                .flatMap(it -> sysUserRoleRepository.deleteByUserId(id));
    }
}
