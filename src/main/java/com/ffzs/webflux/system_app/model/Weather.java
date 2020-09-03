package com.ffzs.webflux.system_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author: ffzs
 * @Date: 2020/9/2 下午5:12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    Long id;
    LocalDateTime date;
    Long direction;
    Long speed;
    Long temperature;
}
