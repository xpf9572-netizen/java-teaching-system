package com.teach.studentadmin.service;

import com.teach.studentadmin.entity.Attendance;
import com.teach.studentadmin.entity.Course;
import com.teach.studentadmin.entity.Student;
import com.teach.studentadmin.repository.AttendanceRepository;
import com.teach.studentadmin.repository.CourseRepository;
import com.teach.studentadmin.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public Page<Attendance> findAll(Map<String, Object> params, int page, int size) {
        Specification<Attendance> spec = (root, query, cb) -> {
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
                if (params.get("status") != null) {
                    predicates.add(cb.equal(root.get("status"), params.get("status")));
                }
                if (params.get("startDate") != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("attendanceDate"),
                            LocalDate.parse(params.get("startDate").toString())));
                }
                if (params.get("endDate") != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("attendanceDate"),
                            LocalDate.parse(params.get("endDate").toString())));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "attendanceDate");
        return attendanceRepository.findAll(spec, PageRequest.of(page, size, sort));
    }

    public List<Attendance> findByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<Attendance> findByCourseId(Long courseId) {
        return attendanceRepository.findByCourseId(courseId);
    }

    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }

    @Transactional
    public Attendance save(Attendance attendance) {
        if (attendance.getId() == null) {
            Student student = studentRepository.findById(attendance.getStudentId())
                    .orElseThrow(() -> new RuntimeException("学生不存在"));
            attendance.setStudentName(student.getName());

            Course course = courseRepository.findById(attendance.getCourseId())
                    .orElseThrow(() -> new RuntimeException("课程不存在"));
            attendance.setCourseName(course.getCourseName());
        }

        if (attendance.getCreateTime() == null) {
            attendance.setCreateTime(java.time.LocalDate.now().toString());
        }

        return attendanceRepository.save(attendance);
    }

    @Transactional
    public void delete(Long id) {
        attendanceRepository.deleteById(id);
    }

    public long count() {
        return attendanceRepository.count();
    }

    public Long countByStudentIdAndStatus(Long studentId, String status) {
        return attendanceRepository.countByStudentIdAndStatus(studentId, status);
    }
}
