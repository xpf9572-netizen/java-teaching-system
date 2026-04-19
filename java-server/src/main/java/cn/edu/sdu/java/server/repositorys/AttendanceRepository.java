package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    @Query("from Attendance where student.personId = :studentId and course.courseId = :courseId and DATE_FORMAT(attendanceDate, '%Y-%m-%d') = :dateStr")
    Optional<Attendance> findByStudentAndCourseAndDate(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId,
            @Param("dateStr") String dateStr);

    @Query("from Attendance where student.personId = :studentId")
    List<Attendance> findByStudentPersonId(@Param("studentId") Integer studentId);
}
