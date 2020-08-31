package com.ffzs.webflux.system_app;

import com.ffzs.webflux.system_app.model.SysUser;
import com.ffzs.webflux.system_app.utils.ReadExcelUtil;
import com.ffzs.webflux.system_app.utils.WriteExcelUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;



@SpringBootApplication
@Slf4j
@AllArgsConstructor
public class SystemAppApplication {

    public static void main(String[] args) {

//        List<Object> list = ReadExcelUtil.readExcel("test-2020-08-311.xlsx", 0, SysUser.class);
//        log.info("{}", list.get(0));
//        WriteExcelUtil.writeExcel("xxx.xlsx", list, SysUser.class);

        SpringApplication.run(SystemAppApplication.class, args);
    }
}
