package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Notice 数据操作接口，主要实现Notice数据的查询操作
 */

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    @Query("from Notice where ?1 = '' or targetAudience = ?1 or targetAudience = 'ALL' order by publishTime desc")
    List<Notice> findNoticeListByAudience(String audience);
}
