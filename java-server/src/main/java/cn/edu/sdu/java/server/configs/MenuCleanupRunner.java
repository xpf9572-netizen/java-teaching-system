package cn.edu.sdu.java.server.configs;

import cn.edu.sdu.java.server.models.MenuInfo;
import cn.edu.sdu.java.server.repositorys.MenuInfoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 清理错误的菜单数据
 */
@Component
public class MenuCleanupRunner implements CommandLineRunner {

    private final MenuInfoRepository menuInfoRepository;

    public MenuCleanupRunner(MenuInfoRepository menuInfoRepository) {
        this.menuInfoRepository = menuInfoRepository;
    }

    @Override
    public void run(String... args) {
        cleanupMenus();
    }

    private void cleanupMenus() {
        System.out.println("=== 清理错误菜单 ===");

        List<MenuInfo> allMenus = menuInfoRepository.findAll();
        Set<Integer> validIds = new HashSet<>();
        for (MenuInfo m : allMenus) {
            if (m.getId() != null) validIds.add(m.getId());
        }

        // 删除孤儿的logout (pid不存在)
        for (MenuInfo m : allMenus) {
            if ("logout".equals(m.getName()) && m.getPid() != null && !validIds.contains(m.getPid())) {
                System.out.println("删除孤立菜单: " + m.getId() + " " + m.getName());
                menuInfoRepository.deleteById(m.getId());
            }
        }

        // 删除 teacher-panel (id=109) - 结构不对
        menuInfoRepository.findById(109).ifPresent(m -> {
            System.out.println("删除错误菜单: " + m.getId() + " " + m.getName());
            menuInfoRepository.deleteById(109);
        });


        // 删除id=108的孤立logout
        menuInfoRepository.findById(108).ifPresent(m -> {
            System.out.println("删除孤立logout: " + m.getId());
            menuInfoRepository.deleteById(108);
        });

        // 修复 WelcomeTeacherPanel 的 pid (应该是6，不是61或null)
        menuInfoRepository.findById(61).ifPresent(m -> {
            if (!"WelcomeTeacherPanel".equals(m.getName())) {
                return;
            }
            // 修复 pid 如果不正确
            if (m.getPid() == null || m.getPid() != 6) {
                System.out.println("修复 WelcomeTeacherPanel pid: " + m.getPid() + " -> 6");
                m.setPid(6);
                menuInfoRepository.save(m);
            }
        });

        // 确保菜单6 (教师管理) 存在
        if (!menuInfoRepository.existsById(6)) {
            System.out.println("创建菜单6 (教师管理)");
            menuInfoRepository.insertMenu6();
        }

        // 确保退出菜单存在于个人信息下 (id=15 for userTypeIds 1,2,3)
        if (!menuInfoRepository.existsById(15)) {
            System.out.println("创建退出菜单 (id=15)");
            menuInfoRepository.insertLogoutMenu();
        }

        System.out.println("=== 清理完成 ===");
    }
}