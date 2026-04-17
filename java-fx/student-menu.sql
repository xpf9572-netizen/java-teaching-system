-- 学生角色菜单初始化脚本
-- 运行此脚本为学生角色添加基础菜单

-- 首先查询当前最大的菜单ID
-- SELECT MAX(id) FROM menu;

-- 插入学生欢迎页面（假设最大ID为100，实际运行时请调整）
INSERT INTO menu (id, pid, name, title, userTypeIds) VALUES (101, NULL, 'welcomeStudentPanel', '首页', '2');

-- 插入子菜单
INSERT INTO menu (id, pid, name, title, userTypeIds) VALUES (102, 101, 'studentScorePanel', '我的成绩', '2');
INSERT INTO menu (id, pid, name, title, userTypeIds) VALUES (103, 101, 'logout', '退出', '2');

-- 注意：userTypeIds 的值需要根据实际的角色ID来确定
-- 可以通过 SELECT * FROM user_type 查看角色ID
-- 学生角色通常是 2 (ROLE_STUDENT)
