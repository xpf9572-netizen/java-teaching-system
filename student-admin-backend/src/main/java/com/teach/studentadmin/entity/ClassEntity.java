package com.teach.studentadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "class_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String classNum;

    @Column(nullable = false, length = 50)
    private String className;

    @Column(length = 50)
    private String department;

    @Column(length = 50)
    private String major;

    @Column(length = 50)
    private String counselor;

    @Column(length = 20)
    private String phone;

    private Integer studentCount;

    @Column(length = 10)
    private String grade;

    @Column(length = 10)
    private String status;

    @Column(name = "create_time")
    private String createTime;
}
