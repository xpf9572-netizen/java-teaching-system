package com.teach.studentadmin.repository;

import com.teach.studentadmin.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByCourseId(Long courseId);

    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.status = :status")
    Long countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") String status);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.courseId = :courseId AND a.attendanceDate = :date")
    Long countByCourseIdAndDate(@Param("courseId") Long courseId, @Param("date") LocalDate date);

    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.courseId = :courseId GROUP BY a.status")
    List<Object[]> countByCourseIdGroupByStatus(@Param("courseId") Long courseId);
}
