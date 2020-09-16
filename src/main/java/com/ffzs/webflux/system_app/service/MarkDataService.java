package com.ffzs.webflux.system_app.service;

import com.ffzs.webflux.system_app.repository.DataChange;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午1:51
 */
@Service
@Order(0)
public class MarkDataService {

    public <T extends DataChange> Mono<T> createObj (T obj) {
        return ReactiveSecurityContextHolder.getContext()
                .map(it->it.getAuthentication().getPrincipal())
                .switchIfEmpty(Mono.just("admin"))
                .map(it -> {
                    LocalDateTime now = LocalDateTime.now();
                    obj.setCreateBy((String)it);
                    obj.setCreateTime(now);
                    obj.setLastUpdateBy((String)it);
                    obj.setLastUpdateTime(LocalDateTime.now());
                    return obj;
                });
    }

    public <T extends DataChange> Mono<T> updateObj (T obj) {
        return ReactiveSecurityContextHolder.getContext()
                .map(it->it.getAuthentication().getPrincipal())
                .switchIfEmpty(Mono.just("admin"))
                .map(it -> {
                    obj.setLastUpdateBy((String)it);
                    obj.setLastUpdateTime(LocalDateTime.now());
                    return obj;
                });
    }
}
