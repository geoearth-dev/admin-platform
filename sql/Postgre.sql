-- --------------------------------------------------------
-- 主机:                           139.186.204.78
-- 服务器版本:                        PostgreSQL 18.4 on x86_64-windows, compiled by msvc-19.44.35227, 64-bit
-- 服务器操作系统:                      
-- HeidiSQL 版本:                  12.20.0.7320
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES  */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 导出  表 public.test_user 结构
CREATE TABLE IF NOT EXISTS "test_user" (
	"id" SERIAL NOT NULL,
	"name" VARCHAR(50) NOT NULL,
	"created_at" TIMESTAMP NOT NULL DEFAULT now(),
	"email" VARCHAR(100) NULL DEFAULT NULL::character varying,
	PRIMARY KEY ("id")
);

-- 正在导出表  public.test_user 的数据：4 rows
INSERT INTO "test_user" ("id", "name", "created_at", "email") VALUES
	(1, '测试', '2026-07-10 11:39:20.527223', 'test@example.com'),
	(2, '张三', '2026-07-20 09:22:20.314776', 'zhangsan@example.com'),
	(3, '李四', '2026-07-20 09:22:20.314776', 'lisi@example.com'),
	(4, '王五', '2026-07-20 09:22:20.314776', 'wangwu@example.com');

