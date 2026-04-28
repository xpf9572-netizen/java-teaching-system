-- 修复菜单数据的SQL脚本
-- 运行此脚本更新菜单的层次结构并添加新课改管理模块菜单

-- 1. 修复父菜单的user_type_ids
UPDATE menu SET user_type_ids = '2' WHERE id = 7;  -- 学生服务只对学生开放
UPDATE menu SET user_type_ids = '1,3' WHERE id = 6;  -- 教师管理对管理员和教师开放

-- 2. 删除可能存在的错误子菜单（如果WelcomeStudentPanel存在但user_type_ids不对）
DELETE FROM menu WHERE name = 'WelcomeStudentPanel' AND user_type_ids != '2';

-- 3. 删除教师欢迎页面旧菜单（如果存在）
DELETE FROM menu WHERE name = 'WelcomeTeacherPanel';

-- 4. 删除可能重复的教师子菜单
DELETE FROM menu WHERE name = 'TeacherCoursePanel' AND user_type_ids != '3';
DELETE FROM menu WHERE name = 'TeacherAttendancePanel' AND user_type_ids != '3';
DELETE FROM menu WHERE name = 'TeacherScorePanel' AND user_type_ids != '3';

-- 5. 删除可能重复的学生子菜单
DELETE FROM menu WHERE name = 'student-leave-panel' AND user_type_ids != '2';
DELETE FROM menu WHERE name = 'student-achievement-panel' AND user_type_ids != '2';
DELETE FROM menu WHERE name = 'student-enrollment-panel' AND user_type_ids != '2';
DELETE FROM menu WHERE name = 'student-schedule-panel' AND user_type_ids != '2';
DELETE FROM menu WHERE name = 'student-attendance-panel' AND user_type_ids != '2';
DELETE FROM menu WHERE name = 'notice-panel' AND user_type_ids != '2';

-- 6. 插入学生欢迎页面和子菜单（如果不存在）
INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 71, 7, 'WelcomeStudentPanel', '首页', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'WelcomeStudentPanel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 72, 71, 'StudentScorePanel', '我的成绩', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'StudentScorePanel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 73, 71, 'student-leave-panel', '学生请假', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'student-leave-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 74, 71, 'student-achievement-panel', '学生成就', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'student-achievement-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 75, 71, 'student-enrollment-panel', '选课管理', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'student-enrollment-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 76, 71, 'student-schedule-panel', '我的课表', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'student-schedule-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 77, 71, 'student-attendance-panel', '考勤查询', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'student-attendance-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 78, 71, 'notice-panel', '通知公告', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'notice-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 79, 71, 'logout', '退出', '2'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'logout' AND pid = 71);

-- 7. 插入教师欢迎页面和子菜单（如果不存在）
INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 61, 6, 'WelcomeTeacherPanel', '首页', '3'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'WelcomeTeacherPanel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 62, 61, 'TeacherCoursePanel', '我的课程', '3'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'TeacherCoursePanel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 63, 61, 'TeacherAttendancePanel', '考勤管理', '3'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'TeacherAttendancePanel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 64, 61, 'TeacherScorePanel', '成绩录入', '3'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'TeacherScorePanel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 65, 61, 'logout', '退出', '3'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'logout' AND pid = 61);

-- 8. 更新现有菜单的user_type_ids（修正错误的教育类型菜单）
UPDATE menu SET user_type_ids = '2' WHERE name = 'StudentScorePanel';

-- 9. 添加新课改管理菜单（如果不存在）
INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 80, 4, 'courseSchedule-panel', '课程安排', '1'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'courseSchedule-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 81, 4, 'examManage-panel', '考试管理', '1'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'examManage-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 82, 4, 'scoreAnalysis-panel', '成绩分析', '1'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'scoreAnalysis-panel');

INSERT INTO menu (id, pid, name, title, user_type_ids)
SELECT 83, 3, 'importExport-panel', '导入导出', '1'
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE name = 'importExport-panel');
