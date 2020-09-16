package com.ffzs.webflux.system_app.config;

import com.ffzs.webflux.system_app.service.SysApiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

/**
 * @author: ffzs
 * @Date: 2020/9/1 下午12:52
 */

@Component
@AllArgsConstructor
@Slf4j
public class prepareRedisData {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final SysApiService sysApiService;

    @PostConstruct
    @Order(2)
    public void route2Redis() {

        sysApiService.findAll()
                .flatMap(api -> {
                    String key = "api_" + api.getUrl().trim() + "_" + api.getRemark();
                    redisTemplate.delete(key);
                    if (!api.getRoles().isEmpty())
                        return redisTemplate.opsForSet()
                                .add(key, api.getRoles().toArray(new String[0]));
                    else return Mono.empty();
                })
                .subscribe();

        log.info("api信息缓存到redis数据库完成。。");
    }
}

