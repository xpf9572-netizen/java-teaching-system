package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * StudentAchievement 学生成就表
 * 存储学生的竞赛获奖、科研论文、专利、项目经历等信息
 */
@Getter
@Setter
@Entity
@Table(name = "student_achievement")
public class StudentAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer achievementId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Size(max = 20)
    private String type;  // COMPETITION-竞赛, PUBLICATION-论文, PATENT-专利, PROJECT-项目

    @Size(max = 100)
    private String name;  // 成果名称

    @Size(max = 50)
    private String level;  // 级别: 国家级, 省级, 校级

    @Size(max = 20)
    private String awardDate;  // 获奖日期

    @Size(max = 500)
    private String description;  // 描述

    @Size(max = 200)
    private String certificateUrl;  // 证书URL

    @Size(max = 20)
    private String status;  // 状态: PENDING-待审核, APPROVED-已审核, REJECTED-已拒绝
}
