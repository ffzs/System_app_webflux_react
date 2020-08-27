package com.ffzs.webflux.system_app.model;

import com.ffzs.webflux.system_app.repository.DataChange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;


@Table("sys_role_api")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleApi implements DataChange {

    @Id
    private long id;

    private long roleId;

    private long apiId;

    public SysRoleApi(long roleId, long apiId) {
        this.roleId = roleId;
        this.apiId = apiId;
    }

    @CreatedBy
    private String createBy;

    @CreatedDate
    private java.time.LocalDateTime createTime;

    @LastModifiedBy
    private String lastUpdateBy;

    @LastModifiedDate
    private java.time.LocalDateTime lastUpdateTime;
}
