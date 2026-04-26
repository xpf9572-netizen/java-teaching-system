package cn.edu.sdu.java.server.configs;

import cn.edu.sdu.java.server.models.MenuInfo;
import cn.edu.sdu.java.server.repositorys.MenuInfoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动时自动修复菜单数据
 */
//@Component  // 已禁用，由SystemService处理菜单初始化
public class MenuDataInitializer implements CommandLineRunner {

    private final MenuInfoRepository menuInfoRepository;

    public MenuDataInitializer(MenuInfoRepository menuInfoRepository) {
        this.menuInfoRepository = menuInfoRepository;
    }

    @Override
    public void run(String... args) {
        try {
            repairMenuData();
        } catch (Exception e) {
            System.err.println("菜单修复过程中出错: " + e.getMessage());
        }
    }

    private void repairMenuData() {
        System.out.println("=== 开始修复菜单数据 ===");

        // 1. 修复父菜单的userTypeIds
        menuInfoRepository.findById(7).ifPresent(menu -> {
            if (!"2".equals(menu.getUserTypeIds())) {
                menu.setUserTypeIds("2");
                menuInfoRepository.saveAndFlush(menu);
                System.out.println("已修复菜单7的userTypeIds为'2'");
            }
        });

        menuInfoRepository.findById(6).ifPresent(menu -> {
            if (!"1,3".equals(menu.getUserTypeIds())) {
                menu.setUserTypeIds("1,3");
                menuInfoRepository.saveAndFlush(menu);
                System.out.println("已修复菜单6的userTypeIds为'1,3'");
            }
        });

        // 2. 删除错误的教师欢迎页面（如果存在）
        List<MenuInfo> existing = menuInfoRepository.findAll();
        for (MenuInfo menu : existing) {
            if (menu.getName() == null) continue;

            if ("WelcomeTeacherPanel".equals(menu.getName())) {
                menuInfoRepository.deleteById(menu.getId());
                System.out.println("已删除 WelcomeTeacherPanel, ID=" + menu.getId());
            }
        }

        // 3. 获取当前最大ID
        int maxId = 100;
        for (MenuInfo menu : menuInfoRepository.findAll()) {
            if (menu.getId() != null && menu.getId() > maxId) {
                maxId = menu.getId();
            }
        }
        System.out.println("当前最大菜单ID: " + maxId);

        // 4. 检查并创建学生欢迎页面
        boolean hasWelcomeStudent = menuInfoRepository.findAll().stream()
            .anyMatch(m -> "WelcomeStudentPanel".equals(m.getName()));

        if (!hasWelcomeStudent) {
            MenuInfo welcomeStudent = new MenuInfo();
            welcomeStudent.setId(++maxId);
            welcomeStudent.setName("WelcomeStudentPanel");
            welcomeStudent.setTitle("首页");
            welcomeStudent.setUserTypeIds("2");
            welcomeStudent.setPid(7);
            welcomeStudent = menuInfoRepository.save(welcomeStudent);
            menuInfoRepository.flush();
            System.out.println("已创建 WelcomeStudentPanel, ID=" + welcomeStudent.getId());

            int parentId = welcomeStudent.getId();
            // 创建学生子菜单
            createMenu(++maxId, parentId, "StudentScorePanel", "我的成绩", "2");
            createMenu(++maxId, parentId, "student-leave-panel", "学生请假", "2");
            createMenu(++maxId, parentId, "student-achievement-panel", "学生成就", "2");
            createMenu(++maxId, parentId, "student-enrollment-panel", "选课管理", "2");
            createMenu(++maxId, parentId, "student-schedule-panel", "我的课表", "2");
            createMenu(++maxId, parentId, "student-attendance-panel", "考勤查询", "2");
            createMenu(++maxId, parentId, "notice-panel", "通知公告", "2");
            createMenu(++maxId, parentId, "logout", "退出", "2");
        }

        // 5. 检查并创建教师欢迎页面
        boolean hasWelcomeTeacher = menuInfoRepository.findAll().stream()
            .anyMatch(m -> "WelcomeTeacherPanel".equals(m.getName()));

        if (!hasWelcomeTeacher) {
            MenuInfo welcomeTeacher = new MenuInfo();
            welcomeTeacher.setId(++maxId);
            welcomeTeacher.setName("WelcomeTeacherPanel");
            welcomeTeacher.setTitle("首页");
            welcomeTeacher.setUserTypeIds("3");
            welcomeTeacher.setPid(6);
            welcomeTeacher = menuInfoRepository.save(welcomeTeacher);
            menuInfoRepository.flush();
            System.out.println("已创建 WelcomeTeacherPanel, ID=" + welcomeTeacher.getId());

            int parentId = welcomeTeacher.getId();
            // 创建教师子菜单
            createMenu(++maxId, parentId, "TeacherCoursePanel", "我的课程", "3");
            createMenu(++maxId, parentId, "TeacherAttendancePanel", "考勤管理", "3");
            createMenu(++maxId, parentId, "TeacherScorePanel", "成绩录入", "3");
            createMenu(++maxId, parentId, "logout", "退出", "3");
        }

        System.out.println("=== 菜单数据修复完成 ===");
    }

    private void createMenu(int id, Integer pid, String name, String title, String userTypeIds) {
        // 检查是否已存在
        boolean exists = menuInfoRepository.findAll().stream()
            .anyMatch(m -> name.equals(m.getName()));

        if (!exists) {
            MenuInfo menu = new MenuInfo();
            menu.setId(id);
            menu.setPid(pid);
            menu.setName(name);
            menu.setTitle(title);
            menu.setUserTypeIds(userTypeIds);
            menuInfoRepository.save(menu);
            menuInfoRepository.flush();
            System.out.println("已创建菜单: " + name + ", ID=" + id);
        }
    }
}