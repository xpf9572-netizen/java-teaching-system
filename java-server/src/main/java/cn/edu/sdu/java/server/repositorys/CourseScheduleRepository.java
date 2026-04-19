package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Integer> {

    List<CourseSchedule> findBySemester(String semester);

    List<CourseSchedule> findByTeacherPersonId(Integer teacherId);

    List<CourseSchedule> findByClassEntityClassId(Integer classId);

    List<CourseSchedule> findByCourseCourseId(Integer courseId);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.semester = ?1 AND cs.dayOfWeek = ?2 AND cs.classPeriod = ?3")
    List<CourseSchedule> findBySemesterAndDayAndPeriod(String semester, String dayOfWeek, String classPeriod);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.teacher.personId = ?1 AND cs.semester = ?2 AND cs.dayOfWeek = ?3 AND cs.classPeriod = ?4")
    List<CourseSchedule> findTeacherConflict(Integer teacherId, String semester, String dayOfWeek, String classPeriod);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.classroom = ?1 AND cs.semester = ?2 AND cs.dayOfWeek = ?3 AND cs.classPeriod = ?4")
    List<CourseSchedule> findClassroomConflict(String classroom, String semester, String dayOfWeek, String classPeriod);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.classEntity.classId = ?1 AND cs.semester = ?2 AND cs.dayOfWeek = ?3 AND cs.classPeriod = ?4")
    List<CourseSchedule> findClassConflict(Integer classId, String semester, String dayOfWeek, String classPeriod);
}
