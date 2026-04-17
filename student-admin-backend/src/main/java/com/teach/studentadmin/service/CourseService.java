package com.teach.studentadmin.service;

import com.teach.studentadmin.entity.Course;
import com.teach.studentadmin.repository.CourseRepository;
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
public class CourseService {

    private final CourseRepository courseRepository;

    public Page<Course> findAll(Map<String, Object> params, int page, int size) {
        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params != null) {
                if (params.get("courseName") != null) {
                    predicates.add(cb.like(root.get("courseName"), "%" + params.get("courseName") + "%"));
                }
                if (params.get("courseNum") != null) {
                    predicates.add(cb.like(root.get("courseNum"), "%" + params.get("courseNum") + "%"));
                }
                if (params.get("department") != null) {
                    predicates.add(cb.equal(root.get("department"), params.get("department")));
                }
                if (params.get("teacherName") != null) {
                    predicates.add(cb.like(root.get("teacherName"), "%" + params.get("teacherName") + "%"));
                }
                if (params.get("courseType") != null) {
                    predicates.add(cb.equal(root.get("courseType"), params.get("courseType")));
                }
                if (params.get("status") != null) {
                    predicates.add(cb.equal(root.get("status"), params.get("status")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return courseRepository.findAll(spec, PageRequest.of(page, size, sort));
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> findByCourseNum(String courseNum) {
        return courseRepository.findByCourseNum(courseNum);
    }

    @Transactional
    public Course save(Course course) {
        if (course.getId() == null && courseRepository.existsByCourseNum(course.getCourseNum())) {
            throw new RuntimeException("课程编号已存在");
        }
        if (course.getCreateTime() == null) {
            course.setCreateTime(java.time.LocalDate.now().toString());
        }
        return courseRepository.save(course);
    }

    @Transactional
    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    public long count() {
        return courseRepository.count();
    }
}
