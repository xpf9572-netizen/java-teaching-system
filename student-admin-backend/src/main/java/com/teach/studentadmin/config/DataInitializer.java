package com.teach.studentadmin.config;

import com.teach.studentadmin.entity.User;
import com.teach.studentadmin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setName("系统管理员");
            admin.setEmail("admin@school.edu");
            admin.setPhone("13800000000");
            admin.setStatus("ACTIVE");
            admin.setCreateTime(java.time.LocalDate.now().toString());
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("teacher")) {
            User teacher = new User();
            teacher.setUsername("teacher");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setRole("TEACHER");
            teacher.setName("张老师");
            teacher.setEmail("teacher@school.edu");
            teacher.setPhone("13800000001");
            teacher.setStatus("ACTIVE");
            teacher.setCreateTime(java.time.LocalDate.now().toString());
            userRepository.save(teacher);
        }

        if (!userRepository.existsByUsername("student")) {
            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setRole("STUDENT");
            student.setName("李同学");
            student.setEmail("student@school.edu");
            student.setPhone("13800000002");
            student.setStatus("ACTIVE");
            student.setCreateTime(java.time.LocalDate.now().toString());
            userRepository.save(student);
        }
    }
}
