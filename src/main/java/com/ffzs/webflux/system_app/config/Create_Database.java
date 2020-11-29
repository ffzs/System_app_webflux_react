package com.ffzs.webflux.system_app.config;

import com.ffzs.webflux.system_app.model.SysApi;
import com.ffzs.webflux.system_app.model.SysRole;
import com.ffzs.webflux.system_app.model.SysUser;
import com.ffzs.webflux.system_app.service.SysApiService;
import com.ffzs.webflux.system_app.service.SysRoleService;
import com.ffzs.webflux.system_app.service.SysUserService;
import com.ffzs.webflux.system_app.utils.ReadExcelUtil;
import io.r2dbc.spi.ConnectionFactories;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

/**
 * @author: ffzs
 * @Date: 2020/9/16 上午8:02
 */

@Component
@Slf4j
@RequiredArgsConstructor
@Order(0)
public class Create_Database {

    @Value("${my.filesPath.sqlScript}")
    private String sqlScriptPath;
    @Value("${my.mysql.host}")
    private String mysqlHost;

    @Value("${my.mysql.database}")
    private String mysqlDatabase;

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysApiService sysApiService;

    @PostConstruct
    public void createTable () throws IOException, InterruptedException {
        DatabaseClient client = DatabaseClient.create(ConnectionFactories
                .get(builder()
                        .option(USER, "root")
                        .option(DRIVER, "mysql")
                        .option(PASSWORD, "123zxc")
                        .option(HOST, mysqlHost)
                        .option(PORT, 3306)
                        .build())
        );

        Boolean hasDatabase = client.execute("SHOW DATABASES")
                .fetch()
                .all()
                .map(it -> it.get("Database"))
                .collectList()
                .map(it->it.contains(mysqlDatabase))
                .block();

        Boolean hasTable = null;
        if (hasDatabase != null && hasDatabase) {
            hasTable = client.execute("USE " + mysqlDatabase + "; show tables like 'sys_user'")
                    .fetch()
                    .all()
                    .collectList()
                    .map(it -> it.size()>0)
                    .block();
        }

        if (!(hasDatabase != null && hasTable !=null && hasTable)) {
            String sqlScript = Files.readString(Path.of(sqlScriptPath));

            client.execute(sqlScript)
                    .fetch()
                    .rowsUpdated()
                    .then(sysUserService.findByUsername("admin"))
                    .switchIfEmpty(
                            initRole()
                                    .then(initUser())
                                    .then(initApi())
                                    .then(Mono.empty())
                    )
                    .block();

            Thread.sleep(2000);
            log.info("数据库以及数据初始化完成。。");
        }
    }

    private Mono<List<SysRole>> initRole () {
        return Mono.fromCallable(()->ReadExcelUtil.readExcel("Data/role_data.xlsx", 0, SysRole.class))
                .flatMap(roleList-> Flux.fromIterable(roleList)
                        .cast(SysRole.class)
                        .map(it -> it.withId(0))
                        .flatMap(sysRoleService::save)
                        .collectList()
                );
    }

    private Mono<List<SysUser>> initUser () {
        return Mono.fromCallable(()->ReadExcelUtil.readExcel("Data/user_data.xlsx", 0, SysUser.class))
                .flatMap(userList-> Flux.fromIterable(userList)
                        .cast(SysUser.class)
                        .map(it -> it.withId(0))
                        .map(it -> it.withPassword("123zxc"))
                        .flatMap(sysUserService::insert)
                        .collectList()
                );
    }

    private Mono<List<SysApi>> initApi () {
        return Mono.fromCallable(()->ReadExcelUtil.readExcel("Data/url_data.xlsx", 0, SysApi.class))
                .flatMap(userList-> Flux.fromIterable(userList)
                        .cast(SysApi.class)
                        .map(it -> it.withId(0))
                        .flatMap(sysApiService::save)
                        .collectList()
                );
    }
}
