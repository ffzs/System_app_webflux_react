package com.ffzs.webflux.system_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: ffzs
 * @Date: 2020/8/27 下午2:38
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUserDetails implements UserDetails {

    private String username;
    private String password;
    private List<String> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

/*  第一个：账户没有过期
    第二个：账户没被锁定 （是否冻结）
    第三个：密码没有过期
    第四个：账户是否可用（是否被删除）*/

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
