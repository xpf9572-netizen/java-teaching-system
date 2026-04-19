package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAchievementRepository extends JpaRepository<StudentAchievement, Integer> {
    List<StudentAchievement> findByStudentPersonId(Integer personId);

    @Query("SELECT sa FROM StudentAchievement sa WHERE sa.student.personId = ?1 ORDER BY sa.awardDate DESC")
    List<StudentAchievement> findByStudentPersonIdOrderByDateDesc(Integer personId);

    List<StudentAchievement> findByStudentPersonIdAndType(Integer personId, String type);
}
