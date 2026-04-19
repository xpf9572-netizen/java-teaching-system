package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {

    List<Exam> findBySemester(String semester);

    @Query("SELECT e FROM Exam e WHERE e.course.courseId = ?1")
    List<Exam> findByCourseId(Integer courseId);

    @Query("SELECT e FROM Exam e WHERE e.invigilator.personId = ?1")
    List<Exam> findByInvigilatorId(Integer invigilatorId);

    @Query("SELECT e FROM Exam e WHERE e.semester = ?1 AND e.examDate = ?2")
    List<Exam> findBySemesterAndDate(String semester, java.util.Date date);
}
