package com.teach.studentadmin.service;

import com.teach.studentadmin.entity.Teacher;
import com.teach.studentadmin.repository.TeacherRepository;
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
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public Page<Teacher> findAll(Map<String, Object> params, int page, int size) {
        Specification<Teacher> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params != null) {
                if (params.get("name") != null) {
                    predicates.add(cb.like(root.get("name"), "%" + params.get("name") + "%"));
                }
                if (params.get("teacherNum") != null) {
                    predicates.add(cb.like(root.get("teacherNum"), "%" + params.get("teacherNum") + "%"));
                }
                if (params.get("department") != null) {
                    predicates.add(cb.equal(root.get("department"), params.get("department")));
                }
                if (params.get("title") != null) {
                    predicates.add(cb.equal(root.get("title"), params.get("title")));
                }
                if (params.get("status") != null) {
                    predicates.add(cb.equal(root.get("status"), params.get("status")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return teacherRepository.findAll(spec, PageRequest.of(page, size, sort));
    }

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    public Optional<Teacher> findByTeacherNum(String teacherNum) {
        return teacherRepository.findByTeacherNum(teacherNum);
    }

    @Transactional
    public Teacher save(Teacher teacher) {
        if (teacher.getId() == null && teacherRepository.existsByTeacherNum(teacher.getTeacherNum())) {
            throw new RuntimeException("教师编号已存在");
        }
        if (teacher.getCreateTime() == null) {
            teacher.setCreateTime(java.time.LocalDate.now().toString());
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }

    public long count() {
        return teacherRepository.count();
    }
}
