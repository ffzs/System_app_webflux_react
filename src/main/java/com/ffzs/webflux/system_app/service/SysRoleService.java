package com.ffzs.webflux.system_app.service;

import com.ffzs.webflux.system_app.model.SysRole;
import com.ffzs.webflux.system_app.repository.SysRoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午4:25
 */

@Service
@AllArgsConstructor
@Slf4j
public class SysRoleService {

    private final SysRoleRepository sysRoleRepository;
    private final MarkDataService mark;

    public Mono<SysRole> findByName (String name) {
        return sysRoleRepository.findByName(name);
    }

    public Mono<SysRole> save (SysRole role) {
        if (role.getId() == 0) {
            return mark.createObj(role)
                    .flatMap(sysRoleRepository::save);
        }
        return mark.updateObj(role)
                .flatMap(sysRoleRepository::save);
    }

    public Flux<SysRole> findAll () {
        return sysRoleRepository.findAll();
    }

    public Mono<Void> delete (Long id) {
        return sysRoleRepository.deleteById(id);
    }
}
