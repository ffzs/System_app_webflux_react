package com.ffzs.webflux.system_app.repository;

import com.ffzs.webflux.system_app.model.SysApi;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午4:34
 */

public interface SysApiRepository extends ReactiveCrudRepository<SysApi, Long> {
    Mono<SysApi> findByUrl(String url);
    Mono<SysApi> findByName(String name);
    Mono<Long> getIdByUrl(String url);
}
