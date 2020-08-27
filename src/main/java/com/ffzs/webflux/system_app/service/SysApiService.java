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

    private Mono<SysApi> saveRoles (SysApi api) {
        List<String> roles = api.getRoles();
        if (roles==null || roles.isEmpty()) return Mono.just(api);
        return Flux.fromIterable(roles)
                .flatMap(role -> sysRoleRepository.findByName(role)
                        .map(SysRole::getId))
                .flatMap(roleId -> sysRoleApiRepository
                        .findByRoleIdAndApiId(roleId, api.getId())
                        .switchIfEmpty(mark
                                .createObj(new SysRoleApi(roleId, api.getId()))
                                .flatMap(sysRoleApiRepository::save)
                        )
                )
                .map(SysRoleApi::getApiId)
                .collectList()
                .then(Mono.just(api));
    }

    public Mono<SysApi> save (SysApi api) {
        Mono<SysApi> monoApi;
        if (api.getId() == 0) {
            monoApi = mark.createObj(api)
                    .flatMap(sysApiRepository::save)
                    .map(SysApi::getName)
                    .flatMap(sysApiRepository::findByName)
                    .map(it -> it.withRoles(api.getRoles()));
        }
        else monoApi =  mark
                .updateObj(api)
                .flatMap(it -> sysApiRepository
                        .findByName(api.getName())
                        .map(oldApi -> it
                                .withCreateBy(oldApi.getCreateBy())
                                .withCreateTime(oldApi.getCreateTime())
                        ))
                .flatMap(sysApiRepository::save);

        return monoApi.flatMap(this::saveRoles);
    }

    public Mono<Void> delete (Long id) {
        return sysApiRepository.deleteById(id);
    }
}
