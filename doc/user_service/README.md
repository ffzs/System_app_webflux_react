# Spring WebFlux + React搭建后台管理系统（2）: 主要业务逻辑实现



上一篇简单实现了数据库表的生成以及生成POJO,这里主要介绍后台服务中实现的接口以及如何通过使用webflux实现一些复杂的逻辑。



## 业务接口

如下为整理的实现的借口汇总，应为是RESTful接口，一个path会对应多个功能：

| Url                  | HttpMethod | 描述                   |
| -------------------- | ---------- | ---------------------- |
| *“/api/auth/logout”* | get        | 登出                   |
| *“/api/auth/login”*  | post       | 登入                   |
| *“/api/user”*        | get        | 通过用户名获取数据用户 |
| *“/api/user”*        | post       | 存储用户               |
| *“/api/user”*        | put        | 更新用户数据           |
| *“/api/user”*        | delete     | 通过id删除用户         |
| *“/api/user/all”*    | get        | 获取全部用户数据       |
| *“/api/role”*        | get        | 通过权限名称获取       |
| *“/api/role”*        | post       | 存储权限               |
| *“/api/role”*        | put        | 更新权限               |
| *“/api/role”*        | delete     | 通过id删除权限         |
| *“/api/role/all”*    | get        | 获取所有权限数据       |
| *“/api/url”*         | get        | 通过url获取接口信息    |
| *“/api/url”*         | post       | 存储接口信息           |
| *“/api/url”*         | put        | 更新接口信息           |
| *“/api/url”*         | delete     | 通过id删除接口信息     |
| *“/api/url/all”*     | get        | 获取所有接口信息       |

## 