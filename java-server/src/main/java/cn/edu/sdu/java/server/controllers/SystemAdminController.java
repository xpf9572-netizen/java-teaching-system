package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.MenuInfo;
import cn.edu.sdu.java.server.repositorys.MenuInfoRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 修复菜单数据 - 重新初始化学生和教师菜单
     * 调用方式: POST /api/admin/repair-menus
     */
    @PostMapping("/repair-menus")
    public Map<String, Object> repairMenus() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 删除错误的学生菜单
            List<MenuInfo> allMenus = menuInfoRepository.findAll();

            // 删除所有 userTypeIds 包含 '1' 但 name 是学生相关的菜单
            for (MenuInfo menu : allMenus) {
                if (menu.getName() != null && menu.getUserTypeIds() != null) {
                    // 删除WelcomeStudentPanel如果不是仅学生
                    if (menu.getName().equals("WelcomeStudentPanel") && !menu.getUserTypeIds().equals("2")) {
                        menuInfoRepository.delete(menu);
                        continue;
                    }
                    // 删除StudentScorePanel如果userTypeIds不对
                    if (menu.getName().equals("StudentScorePanel") && !menu.getUserTypeIds().equals("2")) {
                        menuInfoRepository.delete(menu);
                        continue;
                    }
                    // 删除学生相关菜单如果userTypeIds不是'2'或以'2'开头
                    if (menu.getName() != null && menu.getName().startsWith("student") && menu.getName().endsWith("panel")) {
                        if (menu.getUserTypeIds() != null && !menu.getUserTypeIds().contains("2")) {
                            menuInfoRepository.delete(menu);
                            continue;
                        }
                    }
                }
            }

            // 2. 删除错误的教师欢迎页面
            for (MenuInfo menu : allMenus) {
                if (menu.getName() != null && menu.getName().equals("WelcomeTeacherPanel")) {
                    menuInfoRepository.delete(menu);
                }
            }

            // 3. 删除教师子菜单如果有重复
            for (MenuInfo menu : allMenus) {
                if (menu.getName() != null && menu.getName().startsWith("Teacher")) {
                    menuInfoRepository.delete(menu);
                }
            }

            // 4. 获取最大ID
            int maxId = 100;
            for (MenuInfo menu : allMenus) {
                if (menu.getId() != null && menu.getId() > maxId) {
                    maxId = menu.getId();
                }
            }

            // 5. 创建学生欢迎页面
            MenuInfo welcomeStudent = new MenuInfo();
            welcomeStudent.setId(++maxId);
            welcomeStudent.setName("WelcomeStudentPanel");
            welcomeStudent.setTitle("首页");
            welcomeStudent.setUserTypeIds("2");
            welcomeStudent.setPid(7); // 学生服务
            menuInfoRepository.save(welcomeStudent);

            // 6. 创建学生子菜单
            saveMenu(++maxId, welcomeStudent.getId(), "StudentScorePanel", "我的成绩", "2");
            saveMenu(++maxId, welcomeStudent.getId(), "student-leave-panel", "学生请假", "2");
            saveMenu(++maxId, welcomeStudent.getId(), "student-achievement-panel", "学生成就", "2");
            saveMenu(++maxId, welcomeStudent.getId(), "student-enrollment-panel", "选课管理", "2");
            saveMenu(++maxId, welcomeStudent.getId(), "student-schedule-panel", "我的课表", "2");
            saveMenu(++maxId, welcomeStudent.getId(), "student-attendance-panel", "考勤查询", "2");
            saveMenu(++maxId, welcomeStudent.getId(), "notice-panel", "通知公告", "2");
            saveMenu(++maxId, welcomeStudent.getId(), "logout", "退出", "2");

            // 7. 创建教师欢迎页面
            MenuInfo welcomeTeacher = new MenuInfo();
            welcomeTeacher.setId(++maxId);
            welcomeTeacher.setName("WelcomeTeacherPanel");
            welcomeTeacher.setTitle("首页");
            welcomeTeacher.setUserTypeIds("3");
            welcomeTeacher.setPid(6); // 教师管理
            menuInfoRepository.save(welcomeTeacher);

            // 8. 创建教师子菜单
            saveMenu(++maxId, welcomeTeacher.getId(), "TeacherCoursePanel", "我的课程", "3");
            saveMenu(++maxId, welcomeTeacher.getId(), "TeacherAttendancePanel", "考勤管理", "3");
            saveMenu(++maxId, welcomeTeacher.getId(), "TeacherScorePanel", "成绩录入", "3");
            saveMenu(++maxId, welcomeTeacher.getId(), "logout", "退出", "3");

            result.put("success", true);
            result.put("message", "菜单修复完成!");
            result.put("maxMenuId", maxId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "修复失败: " + e.getMessage());
        }

        return result;
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
    public Map<String, Object> getAllMenus() {
        List<MenuInfo> menus = menuInfoRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", menus);
        result.put("total", menus.size());
        return result;
    }
}