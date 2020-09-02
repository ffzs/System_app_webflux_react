package com.ffzs.webflux.system_app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;



@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class}) // 使用jwt，不再使用UserDetails
public class SystemAppApplication {

    public static void main(String[] args) {

//        List<Object> list = ReadExcelUtil.readExcel("test-2020-08-311.xlsx", 0, SysUser.class);
//        log.info("{}", list.get(0));
//        WriteExcelUtil.writeExcel("xxx.xlsx", list, SysUser.class);

        SpringApplication.run(SystemAppApplication.class, args);
    }
}


