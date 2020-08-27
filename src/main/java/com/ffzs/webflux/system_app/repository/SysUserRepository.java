package com.ffzs.webflux.system_app.repository;

import com.ffzs.webflux.system_app.model.SysUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/26 下午12:51
 */
public interface SysUserRepository extends ReactiveCrudRepository<SysUser, Long> {
    Mono<SysUser> findByUsername(String username);
}
