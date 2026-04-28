-- 为课程表添加教师关联字段
-- 前提是Course模型已添加teacher_id字段

-- 如果course表没有teacher_id列，执行以下SQL添加：
-- ALTER TABLE course ADD COLUMN teacher_id INT COMMENT '授课教师ID';

-- 为Course添加示例数据（可选）
-- INSERT INTO course (num, name, credit, teacher_id) VALUES ('CS101', '数据结构', 4, 1);
