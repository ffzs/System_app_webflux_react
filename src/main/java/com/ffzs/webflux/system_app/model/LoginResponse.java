package com.ffzs.webflux.system_app.model;

import lombok.*;

import java.util.List;

/**
 * @author: ffzs
 * @Date: 2020/8/16 下午9:45
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
public class LoginResponse {

    String username;
    String email;
    String mobile;
    String avatar;
    List<String> roles;
    String token;


    public static LoginResponse fromUser (SysUser user) {
         return LoginResponse.builder()
                 .username(user.getUsername())
                 .email(user.getEmail())
                 .mobile(user.getMobile())
                 .avatar(user.getAvatar())
                 .roles(user.getRoles())
                 .build();
    }
}
