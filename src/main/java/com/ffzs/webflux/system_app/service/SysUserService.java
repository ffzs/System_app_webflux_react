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
                .map(it->it.withPassword(null))
                .flatMap(this::addRoles);
    }

    public Mono<SysUser> findByUsername (String username) {
        return sysUserRepository.findByUsername(username)
                .flatMap(this::addRoles);
    }

    private Mono<Long> checkRole (String role) {
        return sysRoleRepository.findByName(role)
                .switchIfEmpty(
                        mark.createObj(new SysRole(role))
                                .flatMap(sysRoleRepository::save)
                                .map(SysRole::getName)
                                .flatMap(sysRoleRepository::findByName)
                )
                .map(SysRole::getId);
    }


    private Mono<Void> checkUserRole (List<Long> roleIds, Long userId) {

        return sysUserRoleRepository
                .findByUserId(userId)
                .map(SysUserRole::getRoleId)
                .collectList()
                .flatMap(oldRoleIds -> Flux.fromIterable(roleIds)
                        .filter(roleId->!oldRoleIds.contains(roleId))
                        .flatMap(roleId -> mark.createObj(new SysUserRole(userId, roleId)))
                        .cast(SysUserRole.class)
                        .flatMap(sysUserRoleRepository::save)
                        .collectList()
                        .flatMap(it -> Flux
                                .fromIterable(oldRoleIds)
                                .filter(oldRoleId -> !roleIds.contains(oldRoleId))
                                .flatMap(oldRoleId -> sysUserRoleRepository
                                        .deleteByUserIdAndRoleId(userId, oldRoleId)
                                )
                                .collectList()
                        )
                        .then(Mono.empty())
                );
    }

    private Mono<SysUser> saveRoles (SysUser user) {

        List<String> roles = user.getRoles();
        if (roles==null || roles.isEmpty()) return Mono.just(user);
        return Mono.from(
                Flux.fromIterable(roles)
                        .flatMap(this::checkRole)
                        .collectList()
                )
                .flatMap(roleIds-> this.checkUserRole(roleIds, user.getId()))
                .then(Mono.just(user));
    }


    public Mono<SysUser> save (SysUser user) {
        if (user.getId() != 0) {  // id不为0为更新update
            return mark.updateObj(user)
                    .flatMap(it -> sysUserRepository
                            .findByUsername(user.getUsername())
                            .map(oldUser -> {
                                if (it.getPassword() == null || it.getPassword().equals(""))
                                    it.setPassword(oldUser.getPassword());
                                else it.setPassword(password.encode(user.getPassword()));
                                return it
                                    .withCreateBy(oldUser.getCreateBy())
                                    .withCreateTime(oldUser.getCreateTime());}
                            ))
                    .flatMap(sysUserRepository::save)
                    .flatMap(this::saveRoles);
        }
        else {   // id为0为create

            return mark.createObj(user)
                    .map(it->it.withPassword(password.encode(user.getPassword())))
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
