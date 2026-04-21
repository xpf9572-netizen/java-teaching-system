-- 新增表结构SQL脚本
-- 运行此脚本创建新课改管理、考试管理等功能所需的表

-- 1. 课程安排表
DROP TABLE IF EXISTS `course_schedule`;
CREATE TABLE `course_schedule` (
  `schedule_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL,
  `teacher_id` int NOT NULL,
  `class_id` int NOT NULL,
  `classroom` varchar(50) NOT NULL COMMENT '教室',
  `day_of_week` varchar(10) NOT NULL COMMENT '星期几，如：周一、周二',
  `class_period` varchar(20) NOT NULL COMMENT '第几节课，如：1-2节、3-4节',
  `week_range` varchar(50) DEFAULT NULL COMMENT '上课周范围，如：1-16周',
  `semester` varchar(20) NOT NULL COMMENT '学期，如：2024-1',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`schedule_id`),
  KEY `FK_schedule_course` (`course_id`),
  KEY `FK_schedule_teacher` (`teacher_id`),
  KEY `FK_schedule_class` (`class_id`),
  CONSTRAINT `FK_schedule_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
  CONSTRAINT `FK_schedule_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`person_id`),
  CONSTRAINT `FK_schedule_class` FOREIGN KEY (`class_id`) REFERENCES `class_entity` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

-- 2. 考试安排表
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam` (
  `exam_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL,
  `semester` varchar(20) NOT NULL COMMENT '学期',
  `exam_date` date NOT NULL COMMENT '考试日期',
  `exam_time` varchar(30) NOT NULL COMMENT '考试时间，如：09:00-11:00',
  `exam_location` varchar(50) NOT NULL COMMENT '考试地点/考场',
  `invigilator_id` int DEFAULT NULL COMMENT '监考教师ID',
  `exam_type` varchar(20) DEFAULT 'FINAL' COMMENT '考试类型：MIDTERM-期中，FINAL-期末，MAKEUP-补考',
  `total_students` int DEFAULT 0 COMMENT '报考人数',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `version` int DEFAULT 0,
  PRIMARY KEY (`exam_id`),
  KEY `FK_exam_course` (`course_id`),
  KEY `FK_exam_invigilator` (`invigilator_id`),
  CONSTRAINT `FK_exam_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
  CONSTRAINT `FK_exam_invigilator` FOREIGN KEY (`invigilator_id`) REFERENCES `teacher` (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

-- 3. 考试违纪记录表
DROP TABLE IF EXISTS `exam_violation`;
CREATE TABLE `exam_violation` (
  `violation_id` int NOT NULL AUTO_INCREMENT,
  `exam_id` int NOT NULL,
  `student_id` int NOT NULL,
  `violation_type` varchar(30) NOT NULL COMMENT '违纪类型：CHEATING-作弊，LATENESS-迟到，ABSENT-缺考，OTHER-其他',
  `violation_desc` varchar(500) DEFAULT NULL COMMENT '违纪描述',
  `punishment` varchar(200) DEFAULT NULL COMMENT '处理结果',
  `record_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `record_operator` int DEFAULT NULL COMMENT '记录人ID',
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`violation_id`),
  KEY `FK_violation_exam` (`exam_id`),
  KEY `FK_violation_student` (`student_id`),
  CONSTRAINT `FK_violation_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`exam_id`),
  CONSTRAINT `FK_violation_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

-- 4. 成绩分析表（用于存储预计算的分析结果）
DROP TABLE IF EXISTS `score_analysis`;
CREATE TABLE `score_analysis` (
  `analysis_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL,
  `semester` varchar(20) NOT NULL,
  `total_students` int DEFAULT 0,
  `avg_score` double DEFAULT 0,
  `max_score` int DEFAULT 0,
  `min_score` int DEFAULT 0,
  `pass_count` int DEFAULT 0,
  `pass_rate` double DEFAULT 0,
  `score_distribution` varchar(500) DEFAULT NULL COMMENT '分数段分布，JSON格式如：{"<60":5,"60-70":10,"70-80":20}',
  `warning_students` varchar(500) DEFAULT NULL COMMENT '预警学生ID列表，JSON格式',
  `analyze_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`analysis_id`),
  UNIQUE KEY `UK_analysis_course_semester` (`course_id`, `semester`),
  CONSTRAINT `FK_analysis_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;
