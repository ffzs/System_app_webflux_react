package com.ffzs.webflux.system_app.repository;

import java.time.LocalDateTime;

/**
 * @author: ffzs
 * @Date: 2020/8/26 下午11:21
 */

public interface DataChange {

    void setCreateBy(String createBy);

    void setCreateTime(LocalDateTime createTime);

    void setLastUpdateBy(String lastUpdateBy);

    void setLastUpdateTime(LocalDateTime lastUpdateTime);
}
