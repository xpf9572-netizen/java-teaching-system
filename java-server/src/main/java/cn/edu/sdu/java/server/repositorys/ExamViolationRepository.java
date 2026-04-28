package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.ExamViolation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamViolationRepository extends JpaRepository<ExamViolation, Integer> {

    List<ExamViolation> findByExamExamId(Integer examId);

    @Query("SELECT ev FROM ExamViolation ev WHERE ev.student.personId = ?1")
    List<ExamViolation> findByStudentId(Integer studentId);

    @Query("SELECT ev FROM ExamViolation ev WHERE ev.exam.examId = ?1 AND ev.student.personId = ?2")
    List<ExamViolation> findByExamAndStudent(Integer examId, Integer studentId);
}
