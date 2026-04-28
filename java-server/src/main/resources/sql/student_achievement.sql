-- 学生成就表
-- 用于存储学生的竞赛获奖、科研论文、专利、项目经历等信息

CREATE TABLE IF NOT EXISTS student_achievement (
    achievement_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '成就ID',
    student_id INT NOT NULL COMMENT '学生ID (关联person.person_id)',
    type VARCHAR(20) NOT NULL COMMENT '类型: COMPETITION-竞赛, PUBLICATION-论文, PATENT-专利, PROJECT-项目',
    name VARCHAR(100) NOT NULL COMMENT '成果名称',
    level VARCHAR(50) COMMENT '级别: 国家级, 省级, 校级, 院级',
    award_date VARCHAR(20) COMMENT '获奖/发表日期',
    description VARCHAR(500) COMMENT '描述说明',
    certificate_url VARCHAR(200) COMMENT '证书URL',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已审核, REJECTED-已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_id (student_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生成就表';
