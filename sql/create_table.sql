# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists yy_api;

-- 切换库
use yy_api;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    accessKey    varchar(512)                           null comment '访问凭证',
    secretKey    varchar(512)                           null comment '访问密钥',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';

-- 接口信息表
create table if not exists interface_info
(
    `id` bigint not null comment '主键' primary key,
    `name` varchar(256) not null comment '名称',
    `description` varchar(512) null comment '描述',
    `url` varchar(512) not null comment '接口地址',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `status` int default 0 not null comment '状态（0-关闭，1-开启）',
    `method` varchar(256) not null comment '请求方法',
    `userId` bigint not null comment '创建用户 id',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除'
) comment '接口信息' collate = utf8mb4_unicode_ci;

INSERT INTO interface_info (
    id, name, description, url, requestHeader, responseHeader, status, method, userId, createTime, updateTime, isDelete
) VALUES
      (1, '获取用户信息', '根据用户ID查询详细信息', 'https://api.example.com/v1/user/{id}',
       '{"Authorization": "Bearer <token>", "Content-Type": "application/json"}',
       '{"Content-Type": "application/json", "Cache-Control": "no-cache"}',
       1, 'GET', 1001, NOW(), NOW(), 0),

      (2, '创建订单', '提交新订单数据', 'https://api.example.com/v1/order',
       '{"Authorization": "Bearer <token>", "Content-Type": "application/json"}',
       '{"Content-Type": "application/json"}',
       1, 'POST', 1002, NOW(), NOW(), 0),

      (3, '删除商品', '根据商品ID软删除商品', 'https://api.example.com/v1/product/{id}',
       '{"X-API-Key": "your-api-key"}',
       '{"Content-Type": "application/json"}',
       0, 'DELETE', 1001, NOW(), NOW(), 0), -- 状态为关闭

      (4, '上传文件', '上传用户头像图片', 'https://api.example.com/v1/upload/avatar',
       '{"Authorization": "Bearer <token>"}',
       '{"Location": "/files/xxx.jpg"}',
       1, 'POST', 1003, NOW(), NOW(), 0),

      (5, '健康检查', '服务可用性探测接口', 'https://api.example.com/health',
       NULL,
       '{"Content-Type": "application/json"}',
       1, 'GET', 1001, NOW(), NOW(), 0),

      (6, '更新用户资料', '修改用户昵称和邮箱', 'https://api.example.com/v1/user/profile',
       '{"Content-Type": "application/json", "X-Request-ID": "req-123"}',
       '{"Content-Type": "application/json"}',
       0, 'PUT', 1002, NOW(), NOW(), 1); -- 已删除（isDelete=1）