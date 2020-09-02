package com.ffzs.webflux.system_app.service;

import com.ffzs.webflux.system_app.model.SysApi;
import com.ffzs.webflux.system_app.model.SysUser;
import com.ffzs.webflux.system_app.repository.DataChange;
import com.ffzs.webflux.system_app.utils.ReadExcelUtil;
import com.ffzs.webflux.system_app.utils.WriteExcelUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: ffzs
 * @Date: 2020/9/1 下午4:54
 */

@Service
@AllArgsConstructor
@Slf4j
public class IOService {

    public Flux<?> upload(Flux<FilePart> filePart, Class<?> clazz) {

        return filePart
                .flatMap(part -> {
                    try {
                        Path filePath = Files.createTempFile(clazz.getSimpleName(), ".xlsx");
                        part.transferTo(filePath);
                        File file = new File(filePath.toString());
                        InputStream in = new FileInputStream(file);
                        List<Object> user = ReadExcelUtil.readExcel(in, file.getPath(), 0, clazz);
                        if (user != null) return Mono.justOrEmpty(user);
                        in.close();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    return Mono.empty();
                })
                .flatMap(it -> Flux
                        .fromIterable(it)
                        .cast(clazz)
                );
    }


    public Mono<Void> downloadFromDb(List<?> objs, ServerHttpResponse response, Class<? extends DataChange> clazz) throws UnsupportedEncodingException {
        String fileName = new String(("test" + LocalDateTime.now().toLocalDate() + ".xlsx").getBytes(StandardCharsets.UTF_8), "iso8859-1");
        File file = new File(fileName);
        Set<String> banSet = Stream.of("createBy", "createTime", "lastUpdateBy", "lastUpdateTime", "roles", "password", "frozen").collect(Collectors.toSet());

        return isAdmin()
                .flatMap(isAdmin -> {
                    List<String> header = Stream.of(clazz.getDeclaredFields())
                            .map(Field::getName)
                            .filter(it-> isAdmin || !banSet.contains(it))
                            .collect(Collectors.toList());
                    return WriteExcelUtil.data2Workbook(objs, clazz, header);
                })
                .flatMap(workbook -> {
                    try {
                        workbook.write(new FileOutputStream(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return downloadFile(response, file, fileName);
                });
    }


    private Mono<Boolean> isAdmin() {
        return ReactiveSecurityContextHolder.getContext()
                .map(it-> it.getAuthentication()
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
                )
                .map(it -> it.contains("ROLE_ADMIN"));
    }

    private Mono<Void> downloadFile(ServerHttpResponse response, File file, String fileName) {
        ZeroCopyHttpOutputMessage zeroCopyHttpOutputMessage = (ZeroCopyHttpOutputMessage) response;
        try {
            response.getHeaders()
                    .set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(
                            URLEncoder.encode(fileName, StandardCharsets.UTF_8.displayName())));
            return zeroCopyHttpOutputMessage.writeWith(file, 0, file.length());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException();
        }
    }
}
