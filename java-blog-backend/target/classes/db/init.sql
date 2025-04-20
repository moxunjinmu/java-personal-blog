-- 创建数据库
CREATE DATABASE IF NOT EXISTS personal_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE personal_blog;

-- 创建用户表
CREATE TABLE users (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  salt VARCHAR(32) NOT NULL COMMENT '密码盐值',
  avatar VARCHAR(255) DEFAULT NULL COMMENT '用户头像URL',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  bio TEXT DEFAULT NULL COMMENT '个人简介',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建日志分类表
CREATE TABLE blog_categories (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  user_id INT UNSIGNED NOT NULL COMMENT '所属用户ID',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_user_category (user_id, name),
  KEY idx_user_id (user_id),
  CONSTRAINT fk_blog_category_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日志分类表';

-- 创建日志表
CREATE TABLE blogs (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  user_id INT UNSIGNED NOT NULL COMMENT '作者ID',
  category_id INT UNSIGNED NOT NULL COMMENT '分类ID',
  title VARCHAR(255) NOT NULL COMMENT '日志标题',
  content TEXT NOT NULL COMMENT '日志内容',
  allow_comment TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否允许评论：0-不允许，1-允许',
  view_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '阅读量',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_category_id (category_id),
  KEY idx_created_at (created_at),
  CONSTRAINT fk_blog_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_blog_category FOREIGN KEY (category_id) REFERENCES blog_categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日志表';

-- 创建日志图片表
CREATE TABLE blog_images (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  blog_id INT UNSIGNED NOT NULL COMMENT '日志ID',
  image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_blog_id (blog_id),
  CONSTRAINT fk_blog_image FOREIGN KEY (blog_id) REFERENCES blogs (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日志图片表';

-- 创建相册分类表
CREATE TABLE album_categories (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '相册分类ID',
  user_id INT UNSIGNED NOT NULL COMMENT '所属用户ID',
  name VARCHAR(50) NOT NULL COMMENT '相册名称',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_user_album (user_id, name),
  KEY idx_user_id (user_id),
  CONSTRAINT fk_album_category_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相册分类表';

-- 创建相片表
CREATE TABLE photos (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '相片ID',
  user_id INT UNSIGNED NOT NULL COMMENT '所属用户ID',
  category_id INT UNSIGNED NOT NULL COMMENT '相册分类ID',
  title VARCHAR(255) DEFAULT NULL COMMENT '相片标题',
  description TEXT DEFAULT NULL COMMENT '相片描述',
  image_url VARCHAR(255) NOT NULL COMMENT '原图URL',
  thumbnail_url VARCHAR(255) NOT NULL COMMENT '缩略图URL',
  allow_comment TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否允许评论：0-不允许，1-允许',
  view_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '查看次数',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_category_id (category_id),
  KEY idx_created_at (created_at),
  CONSTRAINT fk_photo_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_photo_category FOREIGN KEY (category_id) REFERENCES album_categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相片表';

-- 创建评论表
CREATE TABLE comments (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  content_type ENUM('blog', 'photo') NOT NULL COMMENT '评论内容类型：blog-日志，photo-相片',
  content_id INT UNSIGNED NOT NULL COMMENT '内容ID（日志ID或相片ID）',
  user_id INT UNSIGNED NOT NULL COMMENT '评论者ID',
  parent_id INT UNSIGNED DEFAULT NULL COMMENT '父评论ID，用于回复功能',
  content TEXT NOT NULL COMMENT '评论内容',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_content (content_type, content_id),
  KEY idx_user_id (user_id),
  KEY idx_parent_id (parent_id),
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comments (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 创建留言表
CREATE TABLE messages (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '留言ID',
  user_id INT UNSIGNED NOT NULL COMMENT '留言者ID',
  to_user_id INT UNSIGNED NOT NULL COMMENT '接收留言的用户ID',
  parent_id INT UNSIGNED DEFAULT NULL COMMENT '父留言ID，用于回复功能',
  content TEXT NOT NULL COMMENT '留言内容',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_to_user_id (to_user_id),
  KEY idx_parent_id (parent_id),
  KEY idx_created_at (created_at),
  CONSTRAINT fk_message_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_message_to_user FOREIGN KEY (to_user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_message_parent FOREIGN KEY (parent_id) REFERENCES messages (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言表';

-- 创建访客记录表
CREATE TABLE visitor_records (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  user_id INT UNSIGNED NOT NULL COMMENT '访客ID',
  visited_user_id INT UNSIGNED NOT NULL COMMENT '被访问用户ID',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_visited_user_id (visited_user_id),
  KEY idx_created_at (created_at),
  CONSTRAINT fk_visitor_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_visited_user FOREIGN KEY (visited_user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客记录表';

-- 创建评论通知表
CREATE TABLE comment_notifications (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  user_id INT UNSIGNED NOT NULL COMMENT '接收通知的用户ID',
  comment_id INT UNSIGNED NOT NULL COMMENT '评论ID',
  is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_comment_id (comment_id),
  KEY idx_is_read (is_read),
  CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_notification_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论通知表';