package com.teach.studentadmin.service;

import com.teach.studentadmin.entity.ClassEntity;
import com.teach.studentadmin.repository.ClassRepository;
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
public class ClassService {

    private final ClassRepository classRepository;

    public Page<ClassEntity> findAll(Map<String, Object> params, int page, int size) {
        Specification<ClassEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params != null) {
                if (params.get("className") != null) {
                    predicates.add(cb.like(root.get("className"), "%" + params.get("className") + "%"));
                }
                if (params.get("classNum") != null) {
                    predicates.add(cb.like(root.get("classNum"), "%" + params.get("classNum") + "%"));
                }
                if (params.get("department") != null) {
                    predicates.add(cb.equal(root.get("department"), params.get("department")));
                }
                if (params.get("grade") != null) {
                    predicates.add(cb.equal(root.get("grade"), params.get("grade")));
                }
                if (params.get("status") != null) {
                    predicates.add(cb.equal(root.get("status"), params.get("status")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return classRepository.findAll(spec, PageRequest.of(page, size, sort));
    }

    public List<ClassEntity> findAll() {
        return classRepository.findAll();
    }

    public Optional<ClassEntity> findById(Long id) {
        return classRepository.findById(id);
    }

    public Optional<ClassEntity> findByClassNum(String classNum) {
        return classRepository.findByClassNum(classNum);
    }

    @Transactional
    public ClassEntity save(ClassEntity classEntity) {
        if (classEntity.getId() == null && classRepository.existsByClassNum(classEntity.getClassNum())) {
            throw new RuntimeException("班级编号已存在");
        }
        if (classEntity.getCreateTime() == null) {
            classEntity.setCreateTime(java.time.LocalDate.now().toString());
        }
        return classRepository.save(classEntity);
    }

    @Transactional
    public void delete(Long id) {
        classRepository.deleteById(id);
    }

    public long count() {
        return classRepository.count();
    }
}
