package com.teach.studentadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

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

    private Double score;

    @Column(length = 20)
    private String semester;

    private LocalDate enrollDate;

    @Column(length = 20)
    private String status;

    @Column(name = "create_time")
    private String createTime;
}
