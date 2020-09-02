package com.ffzs.webflux.system_app.repository;

import com.ffzs.webflux.system_app.model.SysUserRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/26 下午1:01
 */

public interface SysUserRoleRepository extends ReactiveCrudRepository<SysUserRole, Long> {
    Flux<SysUserRole> findByUserId(long userId);
    Mono<SysUserRole> findByUserIdAndRoleId(long userId, long roleId);
    Mono<Void> deleteByUserId(Long id);
    Mono<Void> deleteByUserIdAndRoleId(Long userId, Long roleId);
}
