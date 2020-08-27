package com.ffzs.webflux.system_app.repository;

import com.ffzs.webflux.system_app.model.SysRoleApi;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午4:34
 */
public interface SysRoleApiRepository extends ReactiveCrudRepository<SysRoleApi, Long> {
    Flux<SysRoleApi> findByApiId(long id);
    Mono<SysRoleApi> findByRoleIdAndApiId(long roleId, long ApiId);
}
