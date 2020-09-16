
CREATE DATABASE IF NOT EXISTS testdb;
USE testdb;
-- ----------------------------
--  Table structure for `sys_user`
-- ----------------------------

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT COMMENT '编号',
    `username`         varchar(50) NOT NULL COMMENT '用户名',
    `avatar`           varchar(255)         DEFAULT NULL COMMENT '头像地址',
    `password`         varchar(100)         DEFAULT NULL COMMENT '密码',
    `email`            varchar(100)         DEFAULT NULL COMMENT '邮箱',
    `mobile`           varchar(100)         DEFAULT NULL COMMENT '手机号',
    `frozen`           tinyint(4)           DEFAULT '0' COMMENT '账号是否被冻结使用，0：正常， 1：冻结',
    `create_by`        varchar(50)          DEFAULT NULL COMMENT '创建人',
    `create_time`      datetime             DEFAULT NULL COMMENT '创建时间',
    `last_update_by`   varchar(50)          DEFAULT NULL COMMENT '更新人',
    `last_update_time` datetime             DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_username` (`username`),
    UNIQUE INDEX `user_email` (`email`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT ='用户管理';

-- ----------------------------
--  Table structure for `sys_user_role`
-- ----------------------------

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE IF NOT EXISTS `sys_role`
(
    `id`               bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`             varchar(100) DEFAULT NULL COMMENT '角色名称',
    `remark`           varchar(100) DEFAULT NULL COMMENT '备注',
    `create_by`        varchar(50)  DEFAULT NULL COMMENT '创建人',
    `create_time`      datetime     DEFAULT NULL COMMENT '创建时间',
    `last_update_by`   varchar(50)  DEFAULT NULL COMMENT '更新人',
    `last_update_time` datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='角色管理';

-- ----------------------------
--  Table structure for `sys_user_role`
-- ----------------------------

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE IF NOT EXISTS `sys_user_role`
(
    `id`               bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`          bigint  DEFAULT NULL COMMENT '用户ID',
    `role_id`          bigint  DEFAULT NULL COMMENT '角色ID',
    `create_by`        varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time`      datetime    DEFAULT NULL COMMENT '创建时间',
    `last_update_by`   varchar(50) DEFAULT NULL COMMENT '更新人',
    `last_update_time` datetime    DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户角色关联表';


-- ----------------------------
--  Table structure for `sys_api`
-- ----------------------------

DROP TABLE IF EXISTS `sys_api`;
CREATE TABLE IF NOT EXISTS `sys_api`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`             varchar(50)  DEFAULT NULL COMMENT 'api名称',
    `url`              varchar(200) DEFAULT NULL COMMENT 'api的route',
    `remark`           varchar(200) DEFAULT NULL COMMENT '备注：标注api用途',
    `create_by`        varchar(50)  DEFAULT NULL COMMENT '创建人',
    `create_time`      datetime     DEFAULT NULL COMMENT '创建时间',
    `last_update_by`   varchar(50)  DEFAULT NULL COMMENT '更新人',
    `last_update_time` datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='api管理';

-- ----------------------------
--  Table structure for `sys_role_api`
-- ----------------------------

DROP TABLE IF EXISTS `sys_role_api`;
CREATE TABLE IF NOT EXISTS `sys_role_api`
(
    `id`               bigint  NOT NULL AUTO_INCREMENT COMMENT '编号',
    `role_id`          bigint  DEFAULT NULL COMMENT '角色ID',
    `api_id`           bigint  DEFAULT NULL COMMENT 'API ID',
    `create_by`        varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time`      datetime    DEFAULT NULL COMMENT '创建时间',
    `last_update_by`   varchar(50) DEFAULT NULL COMMENT '更新人',
    `last_update_time` datetime    DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX role_id (`role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='api角色映射';
