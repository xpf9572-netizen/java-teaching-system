package com.teach.studentadmin.repository;

import com.teach.studentadmin.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long>, JpaSpecificationExecutor<ClassEntity> {

    Optional<ClassEntity> findByClassNum(String classNum);

    boolean existsByClassNum(String classNum);
}
