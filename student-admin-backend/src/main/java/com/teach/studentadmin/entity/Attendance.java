package com.teach.studentadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "student_name", length = 50)
    private String studentName;

    @Column(name = "course_name", length = 100)
    private String courseName;

    private LocalDate attendanceDate;

    @Column(length = 20)
    private String status;

    @Column(length = 200)
    private String remark;

    @Column(name = "create_time")
    private String createTime;
}
