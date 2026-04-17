package com.teach.studentadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String courseNum;

    @Column(nullable = false, length = 100)
    private String courseName;

    @Column(length = 50)
    private String department;

    private Double credits;

    @Column(length = 50)
    private String teacherName;

    private Long teacherId;

    @Column(length = 10)
    private String courseType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 10)
    private String status;

    @Column(name = "create_time")
    private String createTime;
}
