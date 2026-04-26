-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 202.194.14.120    Database: java_db
-- ------------------------------------------------------
-- Server version	8.0.35-0ubuntu0.23.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `num` varchar(20) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `credit` int DEFAULT NULL,
  `pre_course_id` int DEFAULT NULL,
  `course_path` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`course_id`),
  KEY `FK17gwkcq6pmnubgtpk3yebx740` (`pre_course_id`),
  CONSTRAINT `FK17gwkcq6pmnubgtpk3yebx740` FOREIGN KEY (`pre_course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'001','Java语言',5,NULL,NULL),(2,'002','数学',6,NULL,NULL),(4,'004','操作系统',4,1,NULL),(5,'005','数据结构',4,1,NULL),(6,'006','网络系统',4,NULL,NULL),(7,'007','数据库',4,5,NULL);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dictionary`
--

DROP TABLE IF EXISTS `dictionary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dictionary` (
  `id` int NOT NULL AUTO_INCREMENT,
  `value` varchar(40) DEFAULT NULL,
  `label` varchar(40) DEFAULT NULL,
  `pid` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dictionary`
--

LOCK TABLES `dictionary` WRITE;
/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
INSERT INTO `dictionary` VALUES (1,'XBM','性别码',NULL),(2,'1','男',1),(3,'2','女',1);
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `family_member`
--

DROP TABLE IF EXISTS `family_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `family_member` (
  `member_id` int NOT NULL AUTO_INCREMENT,
  `person_id` int DEFAULT NULL,
  `relation` varchar(10) DEFAULT NULL,
  `name` varchar(30) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `unit` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`member_id`),
  KEY `FK13todxcfo5r0om3iys0c0wad` (`person_id`),
  CONSTRAINT `FK13todxcfo5r0om3iys0c0wad` FOREIGN KEY (`person_id`) REFERENCES `student` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `family_member`
--

LOCK TABLES `family_member` WRITE;
/*!40000 ALTER TABLE `family_member` DISABLE KEYS */;
/*!40000 ALTER TABLE `family_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee`
--

DROP TABLE IF EXISTS `fee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee` (
  `fee_id` int NOT NULL AUTO_INCREMENT,
  `person_id` int DEFAULT NULL,
  `money` double DEFAULT NULL,
  `day` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`fee_id`),
  KEY `FK5pvkbjbnsd5lh8nreh6hhh5b` (`person_id`),
  CONSTRAINT `FK5pvkbjbnsd5lh8nreh6hhh5b` FOREIGN KEY (`person_id`) REFERENCES `student` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee`
--

LOCK TABLES `fee` WRITE;
/*!40000 ALTER TABLE `fee` DISABLE KEYS */;
/*!40000 ALTER TABLE `fee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `title` varchar(40) DEFAULT NULL,
  `pid` int DEFAULT NULL,
  `user_type_ids` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,NULL,'个人信息',NULL,'1,2,3'),(2,NULL,'系统管理',NULL,'1'),(3,NULL,'人员管理',NULL,'1'),(4,NULL,'教务管理',NULL,'1'),(5,NULL,'示例程序',NULL,'1'),(6,NULL,'教师管理',NULL,'1,3'),(7,NULL,'学生服务',NULL,'2'),(11,'system_summary_panel','系统简介',1,'1,2,3'),(12,'base/password-panel','修改密码',1,'1,2,3'),(15,'logout','退出',1,'1,2,3'),(21,'base/menu-panel','菜单管理',2,'1'),(22,'base/dictionary-panel','字典管理',2,'1'),(31,'student-panel','学生管理',3,'1'),(41,'course-panel','课程管理',4,'1'),(42,'score-table-panel','成绩管理',4,'1'),(51,'base/control-demo-panel','组件示例',5,'1'),(61,'WelcomeTeacherPanel','首页',6,'3'),(62,'TeacherCoursePanel','我的课程',61,'3'),(63,'TeacherAttendancePanel','考勤管理',61,'3'),(64,'TeacherScorePanel','成绩录入',61,'3'),(65,'logout','退出',61,'3'),(71,'WelcomeStudentPanel','首页',7,'2'),(72,'StudentScorePanel','我的成绩',71,'2'),(73,'student-leave-panel','学生请假',71,'2'),(74,'student-achievement-panel','学生成就',71,'2'),(75,'student-enrollment-panel','选课管理',71,'2'),(76,'student-schedule-panel','我的课表',71,'2'),(77,'student-attendance-panel','考勤查询',71,'2'),(78,'notice-panel','通知公告',71,'2'),(79,'logout','退出',71,'2');
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `content` varchar(4000) DEFAULT NULL,
  `publisher` varchar(50) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `target_audience` varchar(50) DEFAULT NULL,
  `version` int DEFAULT 0,
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice` VALUES (1,'开学通知','请全体师生于3月1日到校报到','管理员','2024-02-28 10:00:00','IMPORTANT','ALL',0),(2,'清明节放假通知','清明节放假3天','教务处','2024-04-01 09:00:00','HOLIDAY','ALL',0);
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modify_log`
--

DROP TABLE IF EXISTS `modify_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `modify_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(4) DEFAULT NULL,
  `table_name` varchar(20) DEFAULT NULL,
  `info` varchar(2000) DEFAULT NULL,
  `operator_id` int DEFAULT NULL,
  `operate_time` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modify_log`
--

LOCK TABLES `modify_log` WRITE;
/*!40000 ALTER TABLE `modify_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `modify_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person` (
  `person_id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(2) DEFAULT NULL,
  `num` varchar(20) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `dept` varchar(50) DEFAULT NULL,
  `card` varchar(20) DEFAULT NULL,
  `gender` varchar(2) DEFAULT NULL,
  `birthday` varchar(10) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(20) DEFAULT NULL,
  `email` varchar(60) DEFAULT NULL,
  `introduce` varchar(1000) DEFAULT NULL,
  `photo` longblob,
  PRIMARY KEY (`person_id`),
  UNIQUE KEY `UKq0qdoubuenhgp186mv738fo1` (`num`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (1,'0','admin','管理员',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'1','2022030001','杨平','软件学院','1234','1','2001-11-01','11111112','济南高新区舜华路软件学院','1@sdu.edu.cn',NULL,NULL),(3,'1','2022030002','张菊','软件学院','111','2','2001-11-01',NULL,NULL,NULL,NULL,NULL),(4,'1','200799013517','李学庆','软件学院','111','1','1964-04-24',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request_log`
--

DROP TABLE IF EXISTS `request_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url` varchar(100) DEFAULT NULL,
  `username` varchar(20) DEFAULT NULL,
  `start_time` varchar(20) DEFAULT NULL,
  `request_time` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_log`
--

LOCK TABLES `request_log` WRITE;
/*!40000 ALTER TABLE `request_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `request_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `score`
--

DROP TABLE IF EXISTS `score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `score` (
  `score_id` int NOT NULL AUTO_INCREMENT,
  `person_id` int DEFAULT NULL,
  `course_id` int DEFAULT NULL,
  `mark` int DEFAULT NULL,
  `ranking` int DEFAULT NULL,
  PRIMARY KEY (`score_id`),
  KEY `FK4r2i87mwev058q4nvnl36latl` (`course_id`),
  KEY `FKashy91h0e1xtwbe3cev3xpmpx` (`person_id`),
  CONSTRAINT `FK4r2i87mwev058q4nvnl36latl` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
  CONSTRAINT `FKashy91h0e1xtwbe3cev3xpmpx` FOREIGN KEY (`person_id`) REFERENCES `student` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `score`
--

LOCK TABLES `score` WRITE;
/*!40000 ALTER TABLE `score` DISABLE KEYS */;
/*!40000 ALTER TABLE `score` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistics_day`
--

DROP TABLE IF EXISTS `statistics_day`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistics_day` (
  `day` varchar(10) NOT NULL,
  `login_count` int DEFAULT NULL,
  `request_count` int DEFAULT NULL,
  `create_count` int DEFAULT NULL,
  `modify_count` int DEFAULT NULL,
  PRIMARY KEY (`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistics_day`
--

LOCK TABLES `statistics_day` WRITE;
/*!40000 ALTER TABLE `statistics_day` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistics_day` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `person_id` int NOT NULL,
  `major` varchar(20) DEFAULT NULL,
  `class_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (2,'软件工程','软1'),(3,'软件工程','软2');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollment`
--

DROP TABLE IF EXISTS `enrollment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollment` (
  `enrollment_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `course_id` int DEFAULT NULL,
  `score` double DEFAULT NULL,
  `semester` varchar(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `version` int DEFAULT 0,
  PRIMARY KEY (`enrollment_id`),
  KEY `FK_enrollment_student` (`student_id`),
  KEY `FK_enrollment_course` (`course_id`),
  CONSTRAINT `FK_enrollment_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`person_id`),
  CONSTRAINT `FK_enrollment_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollment`
--

LOCK TABLES `enrollment` WRITE;
/*!40000 ALTER TABLE `enrollment` DISABLE KEYS */;
INSERT INTO `enrollment` VALUES (1,2,1,85.5,'2024-1','FINISHED',0),(2,2,2,90.0,'2024-1','FINISHED',0),(3,3,1,78.0,'2024-1','FINISHED',0);
/*!40000 ALTER TABLE `enrollment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `attendance_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `course_id` int DEFAULT NULL,
  `attendance_date` date DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`attendance_id`),
  KEY `FK_attendance_student` (`student_id`),
  KEY `FK_attendance_course` (`course_id`),
  CONSTRAINT `FK_attendance_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`person_id`),
  CONSTRAINT `FK_attendance_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (1,2,1,'2024-03-01','PRESENT',NULL),(2,2,1,'2024-03-08','PRESENT',NULL),(3,2,2,'2024-03-01','ABSENT','病假'),(4,3,1,'2024-03-01','PRESENT',NULL);
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_leave`
--

DROP TABLE IF EXISTS `student_leave`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_leave` (
  `student_leave_id` int NOT NULL AUTO_INCREMENT,
  `admin_comment` varchar(100) DEFAULT NULL,
  `admin_time` datetime(6) DEFAULT NULL,
  `apply_time` datetime(6) DEFAULT NULL,
  `leave_date` varchar(50) DEFAULT NULL,
  `reason` varchar(100) DEFAULT NULL,
  `state` int DEFAULT NULL,
  `teacher_comment` varchar(100) DEFAULT NULL,
  `teacher_time` datetime(6) DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `teacher_id` int DEFAULT NULL,
  `version` int DEFAULT 0,
  PRIMARY KEY (`student_leave_id`),
  KEY `FKjhmm523fqctbt383asjy2b2sb` (`student_id`),
  KEY `FKhumsdcjv9eayw151mw53aht8d` (`teacher_id`),
  CONSTRAINT `FKhumsdcjv9eayw151mw53aht8d` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`person_id`),
  CONSTRAINT `FKjhmm523fqctbt383asjy2b2sb` FOREIGN KEY (`student_id`) REFERENCES `student` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_leave`
--

LOCK TABLES `student_leave` WRITE;
/*!40000 ALTER TABLE `student_leave` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_leave` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_achievement`
--

DROP TABLE IF EXISTS `student_achievement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_achievement` (
  `achievement_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL COMMENT 'COMPETITION-竞赛, PUBLICATION-论文, PATENT-专利, PROJECT-项目',
  `name` varchar(100) DEFAULT NULL COMMENT '成果名称',
  `level` varchar(50) DEFAULT NULL COMMENT '级别: 国家级, 省级, 校级',
  `award_date` varchar(20) DEFAULT NULL COMMENT '获奖日期',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `certificate_url` varchar(200) DEFAULT NULL COMMENT '证书URL',
  `status` varchar(20) DEFAULT NULL COMMENT '状态: PENDING-待审核, APPROVED-已审核, REJECTED-已拒绝',
  PRIMARY KEY (`achievement_id`),
  KEY `FK_achievement_student` (`student_id`),
  CONSTRAINT `FK_achievement_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_achievement`
--

LOCK TABLES `student_achievement` WRITE;
/*!40000 ALTER TABLE `student_achievement` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_achievement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_statistics`
--

DROP TABLE IF EXISTS `student_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_statistics` (
  `statistics_id` int NOT NULL AUTO_INCREMENT,
  `active_count` int DEFAULT NULL,
  `avg_score` double DEFAULT NULL,
  `course_count` int DEFAULT NULL,
  `credit_total` int DEFAULT NULL,
  `gpa` double DEFAULT NULL,
  `person_id` int DEFAULT NULL,
  `leave_count` int DEFAULT NULL,
  `no` int DEFAULT NULL,
  `year` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`statistics_id`),
  UNIQUE KEY `UKin253r8982dm0lvv6janp62au` (`person_id`),
  CONSTRAINT `FK8ijtk08givivxf8q3led6isgw` FOREIGN KEY (`person_id`) REFERENCES `student` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_statistics`
--

LOCK TABLES `student_statistics` WRITE;
/*!40000 ALTER TABLE `student_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_info`
--

DROP TABLE IF EXISTS `system_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `value` varchar(40) DEFAULT NULL,
  `des` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_info`
--

LOCK TABLES `system_info` WRITE;
/*!40000 ALTER TABLE `system_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `person_id` int NOT NULL,
  `degree` varchar(10) DEFAULT NULL,
  `title` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `person_id` int NOT NULL,
  `user_type_id` int DEFAULT NULL,
  `user_name` varchar(20) NOT NULL,
  `password` varchar(60) NOT NULL,
  `create_time` varchar(20) DEFAULT NULL,
  `creator_id` int DEFAULT NULL,
  `last_login_time` varchar(20) DEFAULT NULL,
  `login_count` int DEFAULT NULL,
  PRIMARY KEY (`person_id`),
  UNIQUE KEY `UK4bakctviobmdk6ddh2nwg08c2` (`user_name`),
  KEY `FKlrk9xrdps0emd6d5rx5x3ib6h` (`user_type_id`),
  CONSTRAINT `FKlrk9xrdps0emd6d5rx5x3ib6h` FOREIGN KEY (`user_type_id`) REFERENCES `user_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,1,'admin','$2a$10$FV5lm..jdQWmV7hFguxKDeTrGyiWg1u6HYD2QiQc0tRROrNtSQVOy',NULL,1,'2025-07-16 15:18:04',3),(2,2,'2022030001','$2a$10$FV5lm..jdQWmV7hFguxKDeTrGyiWg1u6HYD2QiQc0tRROrNtSQVOy',NULL,1,NULL,NULL),(3,2,'2022030002','$2a$10$FV5lm..jdQWmV7hFguxKDeTrGyiWg1u6HYD2QiQc0tRROrNtSQVOy',NULL,1,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_type`
--

DROP TABLE IF EXISTS `user_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_type`
--

LOCK TABLES `user_type` WRITE;
/*!40000 ALTER TABLE `user_type` DISABLE KEYS */;
INSERT INTO `user_type` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_STUDENT'),(3,'ROLE_TEACHER');
/*!40000 ALTER TABLE `user_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-20 10:02:13
