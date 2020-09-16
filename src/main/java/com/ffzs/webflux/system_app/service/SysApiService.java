package com.ffzs.webflux.system_app.service;

import com.ffzs.webflux.system_app.model.*;
import com.ffzs.webflux.system_app.repository.SysApiRepository;
import com.ffzs.webflux.system_app.repository.SysRoleApiRepository;
import com.ffzs.webflux.system_app.repository.SysRoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午4:33
 */

@Service
@AllArgsConstructor
@Slf4j
public class SysApiService {

    private final SysApiRepository sysApiRepository;
    private final SysRoleRepository sysRoleRepository;
    private final SysRoleApiRepository sysRoleApiRepository;
    private final MarkDataService mark;

    private Mono<SysApi> addRoles (SysApi api) {

        return sysRoleApiRepository
                .findByApiId(api.getId())
                .map(SysRoleApi::getRoleId)
                .flatMap(sysRoleRepository::findById)
                .map(SysRole::getName)
                .collectList()
                .map(it -> {
                    api.setRoles(it);
                    return api;
                });
    }

    public Flux<SysApi> findAll () {
        return sysApiRepository.findAll()
                .flatMap(this::addRoles);
    }

    public Flux<SysApi> findByUrl (String url) {
        return sysApiRepository.findAllByUrl(url)
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

    private Mono<Void> checkApiRole (List<Long> roleIds, Long apiId) {

        return sysRoleApiRepository
                .findByApiId(apiId)
                .map(SysRoleApi::getRoleId)
                .collectList()
                .flatMap(oldRoleIds -> Flux.fromIterable(roleIds)
                        .filter(roleId->!oldRoleIds.contains(roleId))
                        .flatMap(roleId -> mark.createObj(new SysRoleApi(roleId, apiId)))
                        .cast(SysRoleApi.class)
                        .flatMap(sysRoleApiRepository::save)
                        .collectList()
                        .flatMap(it -> Flux
                                .fromIterable(oldRoleIds)
                                .filter(oldRoleId -> !roleIds.contains(oldRoleId))
                                .flatMap(oldRoleId -> sysRoleApiRepository
                                        .deleteByApiIdAndRoleId(apiId, oldRoleId)
                                )
                                .collectList()
                        )
                        .then(Mono.empty())
                );
    }

    private Mono<SysApi> saveRoles (SysApi api) {

        List<String> roles = api.getRoles();
        if (roles==null || roles.isEmpty()) return Mono.just(api);
        return Mono.from(
                Flux.fromIterable(roles)
                        .flatMap(this::checkRole)
                        .collectList()
        )
                .flatMap(roleIds-> this.checkApiRole(roleIds, api.getId()))
                .then(Mono.just(api));
    }


    public Mono<SysApi> save (SysApi api) {
        if (api.getId() == 0) {
            return mark.createObj(api)
                    .flatMap(sysApiRepository::save)
                    .map(SysApi::getName)
                    .flatMap(sysApiRepository::findByName)
                    .map(it -> it.withRoles(api.getRoles()))
                    .flatMap(this::saveRoles);
        }
        else return mark
                .updateObj(api)
                .flatMap(it -> sysApiRepository
                        .findByName(api.getName())
                        .map(oldApi -> it
                                .withCreateBy(oldApi.getCreateBy())
                                .withCreateTime(oldApi.getCreateTime())
                        ))
                .flatMap(sysApiRepository::save)
                .flatMap(this::saveRoles);
    }

    public Mono<Void> delete (Long id) {
        return sysApiRepository.deleteById(id)
                .flatMap(it->sysRoleApiRepository.deleteByApiId(id));
    }
}
