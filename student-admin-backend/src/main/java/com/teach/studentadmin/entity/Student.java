package com.teach.studentadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String studentNum;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 10)
    private String gender;

    private LocalDate birthday;

    @Column(length = 50)
    private String major;

    @Column(length = 50)
    private String className;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String introduce;

    @Column(length = 50)
    private String idCard;

    @Column(name = "photo_url", length = 200)
    private String photoUrl;

    @Column(length = 10)
    private String status;

    @Column(name = "create_time")
    private String createTime;
}
