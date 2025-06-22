

# 微服务用户权限管理demo

GitHub:https://github.com/makappt/demo

## 功能特性

- **用户管理**：提供用户注册、登录、信息查询和更新等功能。
- **权限控制**：支持基于角色的访问控制（RBAC），包括超级管理员、管理员和普通用户。
- **分布式日志**：通过AOP切面+RocketMQ 异步记录操作日志。
- **服务治理**：使用 Nacos 作为服务注册与发现中心和配置中心。
- **分布式事务**：集成 Seata 保证跨服务操作的事务一致性。
- **数据分库分表**：使用 ShardingSphere 对用户数据进行分库分表。
- **缓存**：使用 Redis 作为缓存中间件，实现单机登录
- **认证**：使用JWT令牌认证

## 

## 快速开始

### 环境准备

- JDK 17
- Maven 3.9
- MySQL 8
- Nacos Server
- RocketMQ
- Redis
- Seata

### 启动步骤：

#### 1.Nacos

cmd 运行

```cpp
startup.cmd -m standalone
```

#### 2.seata

双击启动

```cpp
seata-server.bat
```

#### 3.rocketmq 

cmd运行：

```cpp
start mqnamesrv.cmd
start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true
```

### 4.redis
```cpp
redis-server
```

#### 5.运行sql文件

```cpp
DROP DATABASE IF EXISTS user_db_1;
DROP DATABASE IF EXISTS user_db_2;
DROP DATABASE IF EXISTS user_db_3;
DROP DATABASE IF EXISTS permission_db;
DROP DATABASE IF EXISTS log_db;

CREATE database IF NOT EXISTS user_db_1 DEFAULT CHARSET utf8mb4;
CREATE database IF NOT EXISTS user_db_2 DEFAULT CHARSET utf8mb4;
CREATE database IF NOT EXISTS user_db_3 DEFAULT CHARSET utf8mb4;
CREATE database IF NOT EXISTS permission_db DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS log_db DEFAULT CHARSET utf8mb4;

USE user_db_1;
CREATE TABLE users_0 (
    user_id BIGINT PRIMARY KEY COMMENT '用户id',
    username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
    password VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码',
    email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '邮箱',
    salt VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '随机盐值',
    phone VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '电话号码',
    role_id INT NOT NULL DEFAULT 3 COMMENT '用户权限id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uniq_username` (`username`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户信息表';


CREATE TABLE users_1 (
    user_id BIGINT PRIMARY KEY COMMENT '用户id',
    username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
    password VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码',
    email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '邮箱',
    salt VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '随机盐值',
    phone VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '电话号码',
    role_id INT NOT NULL DEFAULT 3 COMMENT '用户权限id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uniq_username` (`username`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户信息表';


CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log` ADD INDEX `ix_log_created` (`log_created`);


USE user_db_2;
CREATE TABLE users_0 (
    user_id BIGINT PRIMARY KEY COMMENT '用户id',
    username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
    password VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码',
    email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '邮箱',
    salt VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '随机盐值',
    phone VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '电话号码',
    role_id INT NOT NULL DEFAULT 3 COMMENT '用户权限id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uniq_username` (`username`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户信息表';


CREATE TABLE users_1 (
    user_id BIGINT PRIMARY KEY COMMENT '用户id',
    username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
    password VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码',
    email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '邮箱',
    salt VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '随机盐值',
    phone VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '电话号码',
    role_id INT NOT NULL DEFAULT 3 COMMENT '用户权限id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uniq_username` (`username`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户信息表';


CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log` ADD INDEX `ix_log_created` (`log_created`);


USE user_db_3;
CREATE TABLE users_0 (
    user_id BIGINT PRIMARY KEY COMMENT '用户id',
    username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
    password VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码',
    email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '邮箱',
    salt VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '随机盐值',
    phone VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '电话号码',
    role_id INT NOT NULL DEFAULT 3 COMMENT '用户权限id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uniq_username` (`username`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户信息表';


CREATE TABLE users_1 (
    user_id BIGINT PRIMARY KEY COMMENT '用户id',
    username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
    password VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码',
    email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '邮箱',
    salt VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '随机盐值',
    phone VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '电话号码',
    role_id INT NOT NULL DEFAULT 3 COMMENT '用户权限id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uniq_username` (`username`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log` ADD INDEX `ix_log_created` (`log_created`);


USE permission_db;
-- 角色表（权限服务单库）  
CREATE TABLE roles (  
  role_id INT PRIMARY KEY,  -- 1:超管 2:管理员 3: 普通用户
  role_code VARCHAR(20) UNIQUE  -- super_admin/user/admin  
);  

-- 插入超级管理员角色
INSERT INTO roles (role_id, role_code) VALUES (1, 'super_admin');

-- 插入普通用户角色
INSERT INTO roles (role_id, role_code) VALUES (2, 'admin');

-- 插入管理员角色
INSERT INTO roles (role_id, role_code) VALUES (3, 'user');


-- 用户-角色关系表  
CREATE TABLE user_roles (  
  id BIGINT PRIMARY KEY AUTO_INCREMENT,  
  user_id BIGINT,  
  role_id INT,  
  UNIQUE KEY uk_user_role (user_id)  -- 每个用户仅绑定一个角色  
);

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log` ADD INDEX `ix_log_created` (`log_created`);

USE log_db;
-- 操作日志表（单库）
CREATE TABLE operation_logs (
  log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  action VARCHAR(50),  -- 如 "update_user"
  ip VARCHAR(15),
  detail TEXT          -- 记录修改内容（如 {"field":"email", "old":"a","new":"b"}）
);
```



#### 6.添加jvm参数

user-service

```cpp
--add-opens=java.base/java.lang=ALL-UNNAMED 
--add-opens=java.base/java.io=ALL-UNNAMED 
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED 
--add-opens=java.base/java.net=ALL-UNNAMED 
--add-opens=java.base/java.nio=ALL-UNNAMED 
--add-opens=java.base/java.util=ALL-UNNAMED 
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED 
--add-opens=java.base/sun.security.x509=ALL-UNNAMED 
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED 
--add-opens=java.base/javax.net.ssl=ALL-UNNAMED
```

permission-service

```cpp
--add-opens=java.base/java.lang=ALL-UNNAMED
```

#### 
