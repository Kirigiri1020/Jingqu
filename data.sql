-- 景区应用数据库初始化脚本
-- 数据库名：jingqu
-- 创建用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid` varchar(100) NOT NULL COMMENT '微信openid',
  `nick_name` varchar(100) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `is_admin` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是管理员：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- 创建轮播图表
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `image` varchar(500) NOT NULL COMMENT '图片URL',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='轮播图表';

-- 创建景区表
CREATE TABLE `scenic_spot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '景区名称',
  `image` varchar(500) NOT NULL COMMENT '图片URL',
  `tags` varchar(200) DEFAULT NULL COMMENT '标签，逗号分隔',
  `is_recommended` tinyint(1) DEFAULT '0' COMMENT '是否推荐：0-否，1-是',
  `introduction` text COMMENT '景区介绍',
  `open_time` varchar(200) DEFAULT NULL COMMENT '开放时间',
  `address` varchar(500) DEFAULT NULL COMMENT '地址',
  `place` varchar(500) DEFAULT NULL COMMENT '地点',
  `count` double DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景区表';

-- 创建游玩项目表
CREATE TABLE `project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scenic_spot_id` bigint NOT NULL COMMENT '景区ID',
  `title` varchar(100) NOT NULL COMMENT '项目名称',
  `image` varchar(500) NOT NULL COMMENT '图片URL',
  `tag` varchar(50) DEFAULT NULL COMMENT '标签',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `longitude` decimal(10,6) DEFAULT NULL,
  `latitude` decimal(10,6) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_scenic_spot_id` (`scenic_spot_id`),
  KEY `idx_location` (`longitude`,`latitude`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游玩项目表';


-- 创建收藏关系表 (favorites)
CREATE TABLE `favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `scenic_spot_id` bigint NOT NULL COMMENT '景区ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_scenic_spot` (`user_id`,`scenic_spot_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_scenic_spot_id` (`scenic_spot_id`),
  CONSTRAINT `fk_favorites_scenic_spot` FOREIGN KEY (`scenic_spot_id`) REFERENCES `scenic_spot` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏关系表';

INSERT INTO `favorites` (`user_id`, `scenic_spot_id`, `create_time`) VALUES
(3, 4, '2024-01-15 10:30:00'),
(3, 5, '2024-01-15 10:30:00'),
(3, 6, '2024-01-15 10:30:00'),
(3, 7, '2024-01-15 10:30:00'),
(3, 8, '2024-01-15 10:30:00'),
(3, 9, '2024-01-15 10:30:00'),
(3, 10, '2024-01-15 10:30:00'),
(3, 11, '2024-01-15 10:30:00'),
(3, 12, '2024-01-15 10:30:00'),
(3, 13, '2024-01-15 10:30:00'),
(3, 14, '2024-01-15 10:30:00');