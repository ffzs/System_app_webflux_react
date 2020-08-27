package com.ffzs.webflux.system_app.model;

import com.ffzs.webflux.system_app.repository.DataChange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;


@Table("sys_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRole implements DataChange {

    @Id
    private long id;

    private String name;

    private String remark;

    @CreatedBy
    private String createBy;

    @CreatedDate
    private java.time.LocalDateTime createTime;

    @LastModifiedBy
    private String lastUpdateBy;

    @LastModifiedDate
    private java.time.LocalDateTime lastUpdateTime;

    public SysRole(String name) {
        this.name = name;
    }
}
