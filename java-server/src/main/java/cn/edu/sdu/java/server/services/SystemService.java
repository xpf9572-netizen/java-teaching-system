package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.models.EUserType;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

/**
 * SystemService 系统服务行数
 */
@Service
public class SystemService {
    private final DictionaryInfoRepository dictionaryInfoRepository; //数据数据操作自动注入
    private final SystemInfoRepository systemInfoRepository; //数据数据操作自动注入
    private final MenuInfoRepository menuInfoRepository; //菜单数据操作自动注入
    private final ModifyLogRepository modifyLogRepository; //数据数据操作自动注入
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public SystemService(DictionaryInfoRepository dictionaryInfoRepository, SystemInfoRepository systemInfoRepository, ModifyLogRepository modifyLogRepository, MenuInfoRepository menuInfoRepository,
                         PersonRepository personRepository, UserRepository userRepository, TeacherRepository teacherRepository,
                         UserTypeRepository userTypeRepository, PasswordEncoder passwordEncoder) {
        this.dictionaryInfoRepository = dictionaryInfoRepository;
        this.systemInfoRepository = systemInfoRepository;
        this.modifyLogRepository = modifyLogRepository;
        this.menuInfoRepository = menuInfoRepository;
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     *  initDictionary 初始数据字典 在系统初始时将数据字典加载内存，业务处理是直接从内从中获取数数据字典列表和数据字典名称
     */
    public void initDictionary() {
        List<OptionItem> itemList;
        OptionItem item;
        Map<String, String> sMap;
        String value;
        Map<String, List<OptionItem>> dictListMap = ComDataUtil.getInstance().getDictListMap();
        Map<String, Map<String, String>> dictMapMap = ComDataUtil.getInstance().getDictMapMap();
        List<DictionaryInfo>  dList =dictionaryInfoRepository.findRootList();
        List<DictionaryInfo> sList;
        for(DictionaryInfo df : dList) {
            value = df.getValue();
            sMap = new HashMap<String, String>();
            dictMapMap.put(value, sMap);
            itemList = new ArrayList<>();
            dictListMap.put(value, itemList);
            sList = dictionaryInfoRepository.findByPid(df.getId());
            for (DictionaryInfo d : sList) {
                sMap.put(d.getValue(), d.getLabel());
                item = new OptionItem(d.getId(), d.getValue(), d.getLabel());
                itemList.add(item);
            }
        }
        ComDataUtil pi = ComDataUtil.getInstance();
        pi.setDictListMap(dictListMap);
        pi.setDictMapMap(dictMapMap);
    }
    public void initSystem() {
        List<SystemInfo> sList = systemInfoRepository.findAll();
        Map<String,String> map = new HashMap<>();
        for(SystemInfo s:sList) {
            map.put(s.getName(),s.getValue());
        }
        ComDataUtil pi = ComDataUtil.getInstance();
        pi.setSystemMap(map);

        // 确保menu表的id列有AUTO_INCREMENT
        try {
            menuInfoRepository.addAutoIncrement();
        } catch (Exception e) {
            // 如果已经AUTO_INCREMENT，忽略异常
        }

        // 初始化学生菜单
        initStudentMenu();

        // 初始化教师菜单
        initTeacherMenu();

        // 初始化管理员菜单
        initAdminMenu();

        // 初始化默认教师账号
        initDefaultTeacher();
    }

    /**
     * 初始化学生角色菜单
     * 如果学生菜单不存在，则创建；如果存在但缺少子菜单，则补充
     */
    private void initStudentMenu() {
        // 获取最大菜单ID
        Integer maxId = 100;
        List<MenuInfo> allMenus = menuInfoRepository.findAll();
        for (MenuInfo menu : allMenus) {
            if (menu.getId() != null && menu.getId() > maxId) {
                maxId = menu.getId();
            }
        }

        // 查找或创建学生欢迎页面菜单
        MenuInfo welcomeMenu = findOrCreateMenu("WelcomeStudentPanel", "首页", "2", null, maxId);
        if (welcomeMenu == null) {
            return; // 出错
        }
        maxId = Math.max(maxId, welcomeMenu.getId());

        // 查找或创建学生成绩菜单
        findOrCreateMenu("StudentScorePanel", "我的成绩", "2", welcomeMenu.getId(), maxId);

        // 查找或创建学生请假菜单
        findOrCreateMenu("student-leave-panel", "学生请假", "2", welcomeMenu.getId(), maxId);

        // 查找或创建学生成就菜单
        findOrCreateMenu("student-achievement-panel", "学生成就", "2", welcomeMenu.getId(), maxId);

        // 查找或创建选课管理菜单
        findOrCreateMenu("student-enrollment-panel", "选课管理", "2", welcomeMenu.getId(), maxId);

        // 查找或创建我的课表菜单
        findOrCreateMenu("student-schedule-panel", "我的课表", "2", welcomeMenu.getId(), maxId);

        // 查找或创建考勤查询菜单
        findOrCreateMenu("student-attendance-panel", "考勤查询", "2", welcomeMenu.getId(), maxId);

        // 查找或创建通知公告菜单
        findOrCreateMenu("notice-panel", "通知公告", "2", welcomeMenu.getId(), maxId);

        // 查找或创建退出菜单
        findOrCreateMenu("logout", "退出", "2", welcomeMenu.getId(), maxId);
    }

    /**
     * 初始化管理员角色菜单
     * 确保"人员管理"菜单及其子菜单"学生管理"存在
     */
    private void initAdminMenu() {
        // 检查菜单3是否存在（人员管理）
        List<MenuInfo> allMenus = menuInfoRepository.findAll();
        boolean menu3Exists = allMenus.stream().anyMatch(m -> m.getId() == 3);

        if (!menu3Exists) {
            // 创建人员管理菜单
            MenuInfo personMenu = new MenuInfo();
            personMenu.setId(3);
            personMenu.setTitle("人员管理");
            personMenu.setUserTypeIds("1");
            personMenu.setPid(null);
            menuInfoRepository.save(personMenu);
            System.out.println("创建菜单3 (人员管理)");
        }

        // 确保学生管理菜单存在，pid=3
        MenuInfo studentManageMenu = findOrCreateMenu("student-panel", "学生管理", "1", 3, 100);
        if (studentManageMenu != null) {
            System.out.println("学生管理菜单已就绪: id=" + studentManageMenu.getId());
        }
    }

    /**
     * 查找或创建菜单
     * 如果菜单已存在但pid不匹配，自动修正pid
     */
    private MenuInfo findOrCreateMenu(String name, String title, String userTypeIds, Integer pid, Integer maxId) {
        List<MenuInfo> allMenus = menuInfoRepository.findAll();
        for (MenuInfo menu : allMenus) {
            if (menu.getName() != null && menu.getName().equalsIgnoreCase(name)) {
                // 如果存在但userTypeIds或pid不匹配，修正它
                boolean needsUpdate = false;
                if (!userTypeIds.equals(menu.getUserTypeIds())) {
                    menu.setUserTypeIds(userTypeIds);
                    needsUpdate = true;
                }
                // 修正pid（如果pid不匹配且新pid不为null）
                if (pid != null && !pid.equals(menu.getPid())) {
                    menu.setPid(pid);
                    needsUpdate = true;
                }
                if (needsUpdate) {
                    return menuInfoRepository.save(menu);
                }
                return menu;
            }
        }
        // 创建新菜单，使用明确的ID（因为数据库可能没有AUTO_INCREMENT）
        MenuInfo newMenu = new MenuInfo();
        newMenu.setName(name);
        newMenu.setTitle(title);
        newMenu.setUserTypeIds(userTypeIds);
        newMenu.setPid(pid);
        return menuInfoRepository.save(newMenu);
    }

    /**
     * 初始化教师角色菜单
     * 如果教师菜单不存在，则创建；如果存在但缺少子菜单，则补充
     */
    private void initTeacherMenu() {
        // 获取最大菜单ID
        Integer maxId = 100;
        List<MenuInfo> allMenus = menuInfoRepository.findAll();
        for (MenuInfo menu : allMenus) {
            if (menu.getId() != null && menu.getId() > maxId) {
                maxId = menu.getId();
            }
        }

        // 检查菜单6是否存在
        boolean menu6Exists = allMenus.stream().anyMatch(m -> m.getId() == 6);
        if (!menu6Exists) {
            // 使用原生SQL直接插入菜单6，避免Hibernate缓存问题
            menuInfoRepository.insertMenu6();
            System.out.println("创建菜单6 (教师管理) via native SQL");
        }

        // 查找或创建教师欢迎页面菜单
        MenuInfo welcomeMenu = findOrCreateMenu("WelcomeTeacherPanel", "首页", "3", 6, maxId);
        if (welcomeMenu == null) {
            return; // 出错
        }
        maxId = Math.max(maxId, welcomeMenu.getId());

        // 查找或创建我的课程菜单
        findOrCreateMenu("TeacherCoursePanel", "我的课程", "3", welcomeMenu.getId(), maxId);

        // 查找或创建考勤管理菜单
        findOrCreateMenu("TeacherAttendancePanel", "考勤管理", "3", welcomeMenu.getId(), maxId);

        // 查找或创建成绩录入菜单
        findOrCreateMenu("TeacherScorePanel", "成绩录入", "3", welcomeMenu.getId(), maxId);

        // 查找或创建退出菜单
        findOrCreateMenu("logout", "退出", "3", welcomeMenu.getId(), maxId);
    }

    /**
     * 初始化默认教师账号
     * 如果不存在教师账号，则创建一个默认教师账号
     */
    private void initDefaultTeacher() {
        // 检查是否已经有教师账号
        Optional<User> existingUser = userRepository.findByUserName("teacher001");
        if (existingUser.isPresent()) {
            return; // 教师账号已存在，不需要初始化
        }

        // 检查是否已经有教师记录
        Optional<Person> existingTeacher = personRepository.findByNum("teacher001");
        if (existingTeacher.isPresent()) {
            return; // 教师记录已存在，不需要初始化
        }

        // 创建默认教师
        Person person = new Person();
        person.setNum("teacher001");
        person.setType("2"); // 2 = TEACHER类型
        person.setName("张三教师");
        person.setGender("男");
        person.setDept("计算机科学与技术学院");
        person = personRepository.save(person);

        // 创建用户账号
        User user = new User();
        user.setPersonId(person.getPersonId());
        user.setUserName("teacher001");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER.name()));
        user.setCreateTime(DateTimeTool.parseDateTime(new Date()));
        userRepository.save(user);

        // 创建教师信息
        Teacher teacher = new Teacher();
        teacher.setPersonId(person.getPersonId());
        teacher.setPerson(person);
        teacher.setTitle("副教授");
        teacher.setDegree("博士");
        teacherRepository.save(teacher);
    }
    public void modifyLog(Object o, boolean isCreate) {
        String info = CommonMethod.ObjectToJSon(o);
        if(info == null)
            return;
        String tableName = o.getClass().getName();
        int index = tableName.lastIndexOf('.');
        if(index > 0) {
            tableName = tableName.substring(index+1);
        }
        ModifyLog l = new ModifyLog();
        l.setTableName(tableName);
        if(isCreate)
            l.setType("0");
        else
            l.setType("1");
        l.setInfo(info);
        l.setOperateTime(DateTimeTool.parseDateTime(new Date()));
        l.setOperatorId(CommonMethod.getPersonId());
        modifyLogRepository.save(l);
    }
}

