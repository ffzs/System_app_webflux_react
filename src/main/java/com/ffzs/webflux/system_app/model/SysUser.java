package com.ffzs.webflux.system_app.model;

import com.ffzs.webflux.system_app.repository.DataChange;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;


@Table("sys_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
@Builder
public class SysUser implements DataChange {

    @Id
    private long id;

    private String username;

    private String avatar;

//    @JsonIgnore   // 请求返回json不希望显示的字段
    private String password;

    private String email;

    private String mobile;

    private long frozen;

    private String createBy;

    private java.time.LocalDateTime createTime;

    private String lastUpdateBy;

    private java.time.LocalDateTime lastUpdateTime;

    @Transient   // 不存在与数据库中的字段
    private List<String> roles;

}
