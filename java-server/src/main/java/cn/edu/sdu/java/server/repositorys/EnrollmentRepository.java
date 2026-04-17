package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    @Query(value = "from Enrollment where ?1='' or student.person.name like %?1%")
    List<Enrollment> findEnrollmentListByStudentName(String studentName);

    @Query("from Enrollment where student.personId = ?1 and course.courseId = ?2 and semester = ?3")
    Optional<Enrollment> findByStudentAndCourseAndSemester(Integer studentId, Integer courseId, String semester);
}
