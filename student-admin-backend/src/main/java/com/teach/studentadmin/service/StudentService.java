package com.teach.studentadmin.service;

import com.teach.studentadmin.entity.Student;
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
public class StudentService {

    private final StudentRepository studentRepository;

    public Page<Student> findAll(Map<String, Object> params, int page, int size) {
        Specification<Student> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params != null) {
                if (params.get("name") != null) {
                    predicates.add(cb.like(root.get("name"), "%" + params.get("name") + "%"));
                }
                if (params.get("studentNum") != null) {
                    predicates.add(cb.like(root.get("studentNum"), "%" + params.get("studentNum") + "%"));
                }
                if (params.get("className") != null) {
                    predicates.add(cb.like(root.get("className"), "%" + params.get("className") + "%"));
                }
                if (params.get("major") != null) {
                    predicates.add(cb.equal(root.get("major"), params.get("major")));
                }
                if (params.get("gender") != null) {
                    predicates.add(cb.equal(root.get("gender"), params.get("gender")));
                }
                if (params.get("status") != null) {
                    predicates.add(cb.equal(root.get("status"), params.get("status")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return studentRepository.findAll(spec, PageRequest.of(page, size, sort));
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> findByStudentNum(String studentNum) {
        return studentRepository.findByStudentNum(studentNum);
    }

    @Transactional
    public Student save(Student student) {
        if (student.getId() == null && studentRepository.existsByStudentNum(student.getStudentNum())) {
            throw new RuntimeException("学号已存在");
        }
        if (student.getCreateTime() == null) {
            student.setCreateTime(java.time.LocalDate.now().toString());
        }
        return studentRepository.save(student);
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    public long count() {
        return studentRepository.count();
    }

    public long countByClassName(String className) {
        Specification<Student> spec = (root, query, cb) ->
                cb.equal(root.get("className"), className);
        return studentRepository.count(spec);
    }
}
