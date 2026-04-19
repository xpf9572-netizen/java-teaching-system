package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * CourseSchedule 课程安排表实体类
 * 管理课程的上课时间、地点、教师和班级安排
 */
@Getter
@Setter
@Entity
@Table(name = "course_schedule")
public class CourseSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @NotBlank
    @Column(name = "classroom")
    private String classroom;

    @NotBlank
    @Column(name = "day_of_week")
    private String dayOfWeek;

    @NotBlank
    @Column(name = "class_period")
    private String classPeriod;

    @Column(name = "week_range")
    private String weekRange;

    @Column(name = "semester")
    private String semester;

    @Column(name = "remark")
    private String remark;
}
