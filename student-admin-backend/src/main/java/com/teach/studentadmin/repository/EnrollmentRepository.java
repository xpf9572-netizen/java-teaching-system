package com.teach.studentadmin.repository;

import com.teach.studentadmin.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.courseId = :courseId AND e.semester = :semester")
    List<Enrollment> findByCourseIdAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);

    @Query("SELECT AVG(e.score) FROM Enrollment e WHERE e.courseId = :courseId AND e.score IS NOT NULL")
    Double findAverageScoreByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT MAX(e.score) FROM Enrollment e WHERE e.courseId = :courseId AND e.score IS NOT NULL")
    Double findMaxScoreByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT MIN(e.score) FROM Enrollment e WHERE e.courseId = :courseId AND e.score IS NOT NULL")
    Double findMinScoreByCourseId(@Param("courseId") Long courseId);
}
