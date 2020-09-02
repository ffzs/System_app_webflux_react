package com.ffzs.webflux.system_app.controller;

import com.ffzs.webflux.system_app.model.SysApi;
import com.ffzs.webflux.system_app.model.SysRole;
import com.ffzs.webflux.system_app.model.SysUser;
import com.ffzs.webflux.system_app.service.IOService;
import com.ffzs.webflux.system_app.service.SysApiService;
import com.ffzs.webflux.system_app.service.SysRoleService;
import com.ffzs.webflux.system_app.service.SysUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;


/**
 * @author: ffzs
 * @Date: 2020/8/30 下午9:37
 */

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/io")
public class IOController {

    private final SysUserService sysUserService;
    private final IOService ioService;
    private final SysApiService sysApiService;
    private final SysRoleService sysRoleService;

    @PostMapping(value = "/upload/user/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Flux<SysUser> uploadUser(@RequestPart("file") Flux<FilePart> filePart){

        return ioService.upload(filePart, SysUser.class)
                .cast(SysUser.class);
    }

    @PostMapping(value = "/upload/url/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Flux<SysApi> uploadUrl(@RequestPart("file") Flux<FilePart> filePart){

        return ioService.upload(filePart, SysApi.class)
                .cast(SysApi.class);
    }

    @PostMapping(value = "/upload/role/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Flux<SysRole> uploadRole(@RequestPart("file") Flux<FilePart> filePart){

        return ioService.upload(filePart, SysRole.class)
                .cast(SysRole.class);
    }


    @PostMapping("/download/user/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'IT', 'HR')")
    public Mono<Void> downloadUser(ServerHttpResponse response) {

        return sysUserService.findAll()
                .collectList()
                .flatMap(objs-> {
                    try {
                        return ioService.downloadFromDb(objs, response, SysUser.class);
                    } catch (UnsupportedEncodingException e) {
                        return Mono.error(new UnsupportedEncodingException());
                    }
                });
    }


    @PostMapping("/download/role/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'IT', 'HR')")
    public Mono<Void> downloadRole(ServerHttpResponse response) {

        return sysRoleService.findAll()
                .collectList()
                .flatMap(objs-> {
                    try {
                        return ioService.downloadFromDb(objs, response, SysRole.class);
                    } catch (UnsupportedEncodingException e) {
                        return Mono.error(new UnsupportedEncodingException());
                    }
                });
    }


    @PostMapping("/download/url/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'IT', 'HR')")
    public Mono<Void> downloadUrl(ServerHttpResponse response) {

        return sysApiService.findAll()
                .collectList()
                .flatMap(objs-> {
                    try {
                        return ioService.downloadFromDb(objs, response, SysApi.class);
                    } catch (UnsupportedEncodingException e) {
                        return Mono.error(new UnsupportedEncodingException());
                    }
                });
    }

}
