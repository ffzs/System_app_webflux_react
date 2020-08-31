package com.ffzs.webflux.system_app.controller;

import com.ffzs.webflux.system_app.model.SysUser;
import com.ffzs.webflux.system_app.service.SysUserService;
import com.ffzs.webflux.system_app.utils.ReadExcelUtil;
import com.ffzs.webflux.system_app.utils.WriteExcelUtil;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping(value = "/upload/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<SysUser> upload(@RequestPart("file") Flux<FilePart> filePart){

        return filePart
                .flatMap(part -> {
                    try {
                        Path filePath = Files.createTempFile("",".xlsx");
                        part.transferTo(filePath);
                        File file = new File(filePath.toString());
                        InputStream in = new FileInputStream(file);
                        List<Object> user = ReadExcelUtil.readExcel(in, file.getPath(), 0, SysUser.class);
                        if (user != null) return Mono.justOrEmpty(user);
                        in.close();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    return Mono.empty();
                })
                .flatMap(it -> Flux
                        .fromIterable(it)
                        .cast(SysUser.class)
                );
    }


    @PostMapping("/download/excel/db")
    public Mono<Void> downloadFromDb(ServerHttpResponse response) throws UnsupportedEncodingException {
        String fileName = new String(("test-" + LocalDateTime.now().toLocalDate() + ".xlsx").getBytes(StandardCharsets.UTF_8),"iso8859-1");
        File file = new File(fileName);
        return sysUserService.findAll()
                .collectList()
                .flatMap(list -> WriteExcelUtil.data2Workbook(list, SysUser.class))
                .flatMap(workbook-> {
                    try {
                        workbook.write(new FileOutputStream(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return downloadFile(response, file, fileName);
                });
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
