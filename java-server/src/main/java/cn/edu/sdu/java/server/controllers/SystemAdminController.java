package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.MenuInfo;
import cn.edu.sdu.java.server.repositorys.MenuInfoRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统维护接口 - 用于修复和初始化菜单数据
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class SystemAdminController {

    @Autowired
    private MenuInfoRepository menuInfoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 修复菜单数据 - 重新初始化学生和教师菜单
     * 调用方式: POST /api/admin/repair-menus
     */
    @PostMapping("/repair-menus")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Map<String, Object> repairMenus() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 用原生SQL删除所有要重建的菜单（避免Hibernate实体状态冲突）
            String[] targetNames = {
                "WelcomeStudentPanel", "welcome-student-panel",
                "StudentScorePanel", "student-score-panel", "studentScorePanel",
                "student-leave-panel", "student-achievement-panel",
                "student-enrollment-panel", "student-schedule-panel",
                "student-attendance-panel", "notice-panel",
                "WelcomeTeacherPanel", "welcome-teacher-panel",
                "TeacherCoursePanel", "teacher-course-panel",
                "TeacherAttendancePanel", "teacher-attendance-panel",
                "TeacherScorePanel", "teacher-score-panel"
            };

            int deletedCount = 0;
            for (String name : targetNames) {
                deletedCount += entityManager.createNativeQuery(
                    "DELETE FROM menu WHERE name = :name")
                    .setParameter("name", name)
                    .executeUpdate();
            }
            // 删除学生/教师下的退出菜单（保留个人信息下id=15那个）
            deletedCount += entityManager.createNativeQuery(
                "DELETE FROM menu WHERE name = 'logout' AND (pid IS NULL OR pid != 1)")
                .executeUpdate();
            // 删除管理员个人信息下多余的退出菜单（保留userTypeIds='1,2,3'的，删除userTypeIds='1'的）
            deletedCount += entityManager.createNativeQuery(
                "DELETE FROM menu WHERE name = 'logout' AND pid = 1 AND user_type_ids = '1'")
                .executeUpdate();

            CommonMethod.logDeleteOperation("menu_info", "repair_deleted:" + deletedCount);

            // 删除空菜单"教务管理" (id=4) - 无子菜单，无法使用
            int deletedId4 = entityManager.createNativeQuery(
                "DELETE FROM menu WHERE id = 4").executeUpdate();
            if (deletedId4 > 0) {
                CommonMethod.logDeleteOperation("menu_info", "deleted_id4_academic_management");
            }

            // 修正"教师管理" (id=6) 仅教师可见（移除管理员可见）
            entityManager.createNativeQuery(
                "UPDATE menu SET user_type_ids = '3' WHERE id = 6 AND user_type_ids = '1,3'")
                .executeUpdate();

            // 获取当前最大ID
            Number maxIdObj = (Number) entityManager.createNativeQuery(
                "SELECT COALESCE(MAX(id), 100) FROM menu").getSingleResult();
            int maxId = maxIdObj.intValue();

            // 创建学生欢迎页面（作为根菜单，方便学生直接访问）
            entityManager.createNativeQuery(
                "INSERT INTO menu (id, name, title, pid, user_type_ids) VALUES (?, 'WelcomeStudentPanel', '首页', NULL, '2')")
                .setParameter(1, ++maxId).executeUpdate();
            int welcomeStudentId = maxId;

            // 创建学生子菜单
            saveMenuNative(++maxId, welcomeStudentId, "StudentScorePanel", "我的成绩", "2");
            saveMenuNative(++maxId, welcomeStudentId, "student-leave-panel", "学生请假", "2");
            saveMenuNative(++maxId, welcomeStudentId, "student-achievement-panel", "学生成就", "2");
            saveMenuNative(++maxId, welcomeStudentId, "student-enrollment-panel", "选课管理", "2");
            saveMenuNative(++maxId, welcomeStudentId, "student-schedule-panel", "我的课表", "2");
            saveMenuNative(++maxId, welcomeStudentId, "student-attendance-panel", "考勤查询", "2");
            saveMenuNative(++maxId, welcomeStudentId, "notice-panel", "通知公告", "2");
            saveMenuNative(++maxId, welcomeStudentId, "logout", "退出", "2");

            // 创建教师欢迎页面
            entityManager.createNativeQuery(
                "INSERT INTO menu (id, name, title, pid, user_type_ids) VALUES (?, 'WelcomeTeacherPanel', '首页', 6, '3')")
                .setParameter(1, ++maxId).executeUpdate();
            int welcomeTeacherId = maxId;

            // 创建教师子菜单
            saveMenuNative(++maxId, welcomeTeacherId, "TeacherCoursePanel", "我的课程", "3");
            saveMenuNative(++maxId, welcomeTeacherId, "TeacherAttendancePanel", "考勤管理", "3");
            saveMenuNative(++maxId, welcomeTeacherId, "TeacherScorePanel", "成绩录入", "3");
            saveMenuNative(++maxId, welcomeTeacherId, "logout", "退出", "3");

            result.put("success", true);
            result.put("message", "菜单修复完成! 删除了" + deletedCount + "个旧菜单");
            result.put("maxMenuId", maxId);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "修复失败: " + e.getMessage());
        }

        return result;
    }

    private void saveMenuNative(int id, Integer pid, String name, String title, String userTypeIds) {
        entityManager.createNativeQuery(
            "INSERT INTO menu (id, name, title, pid, user_type_ids) VALUES (?, ?, ?, ?, ?)")
            .setParameter(1, id)
            .setParameter(2, name)
            .setParameter(3, title)
            .setParameter(4, pid)
            .setParameter(5, userTypeIds)
            .executeUpdate();
    }

    private void saveMenu(int id, Integer pid, String name, String title, String userTypeIds) {
        MenuInfo menu = new MenuInfo();
        menu.setId(id);
        menu.setPid(pid);
        menu.setName(name);
        menu.setTitle(title);
        menu.setUserTypeIds(userTypeIds);
        menuInfoRepository.save(menu);
    }

    /**
     * 查看当前所有菜单
     */
    @GetMapping("/menus")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getAllMenus() {
        List<MenuInfo> menus = menuInfoRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", menus);
        result.put("total", menus.size());
        return result;
    }
}