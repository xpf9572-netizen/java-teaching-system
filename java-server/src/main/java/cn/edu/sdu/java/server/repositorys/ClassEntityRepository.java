package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ClassEntityRepository extends JpaRepository<ClassEntity, Integer> {
    @Query(value = "from ClassEntity where ?1='' or classNum like %?1% or className like %?1%")
    List<ClassEntity> findClassEntityListByNumName(String numName);
}
