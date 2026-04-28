package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.MenuInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/*
 * MenuInfo 数据操作接口，主要实现MenuInfo数据的查询操作
 */
public interface MenuInfoRepository extends JpaRepository<MenuInfo,Integer> {
    @Query(value=" from MenuInfo where (pid is null or pid=0) and (?1='' or userTypeIds like concat('%', ?1, '%'))")
    List<MenuInfo> findByUserTypeIds(String userTypeIds);
    @Query(value=" from MenuInfo where pid =?2 and (?1='' or userTypeIds like concat('%', ?1, '%'))")
    List<MenuInfo> findByUserTypeIds(String userTypeIds, Integer pid);

    int countMenuInfoByPid(Integer pid);

    @Query(value = "SELECT COALESCE(MAX(m.id), 0) FROM MenuInfo m")
    int getMaxId();

    @Modifying
    @Transactional
    @Query(value="ALTER TABLE menu MODIFY COLUMN id INT AUTO_INCREMENT", nativeQuery = true)
    void addAutoIncrement();

    @Modifying
    @Transactional
    @Query(value="INSERT INTO menu (id, name, title, pid, user_type_ids) VALUES (6, NULL, '教师管理', NULL, '3')", nativeQuery = true)
    void insertMenu6();

    @Modifying
    @Transactional
    @Query(value="INSERT INTO menu (id, name, title, pid, user_type_ids) VALUES (15, 'logout', '退出', 1, '1,2,3')", nativeQuery = true)
    void insertLogoutMenu();

    List<MenuInfo> findByName(String name);

    List<MenuInfo> findByPid(Integer pid);
}
