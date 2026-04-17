package com.teach.studentadmin.service;

import com.teach.studentadmin.entity.Course;
import com.teach.studentadmin.entity.Enrollment;
import com.teach.studentadmin.entity.Student;
import com.teach.studentadmin.repository.CourseRepository;
import com.teach.studentadmin.repository.EnrollmentRepository;
import com.teach.studentadmin.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public Page<Enrollment> findAll(Map<String, Object> params, int page, int size) {
        Specification<Enrollment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params != null) {
                if (params.get("studentName") != null) {
                    predicates.add(cb.like(root.get("studentName"), "%" + params.get("studentName") + "%"));
                }
                if (params.get("courseName") != null) {
                    predicates.add(cb.like(root.get("courseName"), "%" + params.get("courseName") + "%"));
                }
                if (params.get("studentId") != null) {
                    predicates.add(cb.equal(root.get("studentId"), params.get("studentId")));
                }
                if (params.get("courseId") != null) {
                    predicates.add(cb.equal(root.get("courseId"), params.get("courseId")));
                }
                if (params.get("semester") != null) {
                    predicates.add(cb.equal(root.get("semester"), params.get("semester")));
                }
                if (params.get("status") != null) {
                    predicates.add(cb.equal(root.get("status"), params.get("status")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return enrollmentRepository.findAll(spec, PageRequest.of(page, size, sort));
    }

    public List<Enrollment> findByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> findByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public Optional<Enrollment> findById(Long id) {
        return enrollmentRepository.findById(id);
    }

    public Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Transactional
    public Enrollment save(Enrollment enrollment) {
        if (enrollment.getId() == null) {
            if (enrollmentRepository.findByStudentIdAndCourseId(enrollment.getStudentId(), enrollment.getCourseId()).isPresent()) {
                throw new RuntimeException("该学生已选择此课程");
            }

            Student student = studentRepository.findById(enrollment.getStudentId())
                    .orElseThrow(() -> new RuntimeException("学生不存在"));
            enrollment.setStudentName(student.getName());

            Course course = courseRepository.findById(enrollment.getCourseId())
                    .orElseThrow(() -> new RuntimeException("课程不存在"));
            enrollment.setCourseName(course.getCourseName());
        }

        if (enrollment.getCreateTime() == null) {
            enrollment.setCreateTime(java.time.LocalDate.now().toString());
        }
        if (enrollment.getEnrollDate() == null) {
            enrollment.setEnrollDate(java.time.LocalDate.now());
        }

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void delete(Long id) {
        enrollmentRepository.deleteById(id);
    }

    public long count() {
        return enrollmentRepository.count();
    }

    public Double getAverageScoreByCourseId(Long courseId) {
        return enrollmentRepository.findAverageScoreByCourseId(courseId);
    }

    public Double getMaxScoreByCourseId(Long courseId) {
        return enrollmentRepository.findMaxScoreByCourseId(courseId);
    }

    public Double getMinScoreByCourseId(Long courseId) {
        return enrollmentRepository.findMinScoreByCourseId(courseId);
    }
}
