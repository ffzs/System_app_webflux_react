package com.ffzs.webflux.system_app.repository;

import com.ffzs.webflux.system_app.model.SysRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/26 下午12:53
 */
public interface SysRoleRepository extends ReactiveCrudRepository<SysRole, Long> {
    Mono<SysRole> findByName(String name);

}
