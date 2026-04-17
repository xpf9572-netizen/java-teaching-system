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

        // 初始化学生菜单
        initStudentMenu();

        // 初始化教师菜单
        initTeacherMenu();

        // 初始化默认教师账号
        initDefaultTeacher();
    }

    /**
     * 初始化学生角色菜单
     * 如果学生菜单不存在，则创建
     */
    private void initStudentMenu() {
        // 检查是否已经有学生菜单（通过查找名为WelcomeStudentPanel或welcomeStudentPanel的菜单，忽略大小写）
        List<MenuInfo> existingMenus = menuInfoRepository.findByUserTypeIds("2");
        for (MenuInfo menu : existingMenus) {
            if (menu.getName() != null && menu.getName().equalsIgnoreCase("WelcomeStudentPanel")) {
                return; // 菜单已存在，不需要初始化
            }
        }

        // 获取最大菜单ID
        Integer maxId = 100;
        List<MenuInfo> allMenus = menuInfoRepository.findAll();
        for (MenuInfo menu : allMenus) {
            if (menu.getId() != null && menu.getId() > maxId) {
                maxId = menu.getId();
            }
        }

        // 创建学生欢迎页面菜单
        MenuInfo welcomeMenu = new MenuInfo();
        welcomeMenu.setId(++maxId);
        welcomeMenu.setName("WelcomeStudentPanel");
        welcomeMenu.setTitle("首页");
        welcomeMenu.setUserTypeIds("2"); // 2 = STUDENT角色
        welcomeMenu.setPid(null);
        menuInfoRepository.save(welcomeMenu);

        // 创建学生成绩菜单
        MenuInfo scoreMenu = new MenuInfo();
        scoreMenu.setId(++maxId);
        scoreMenu.setName("StudentScorePanel");
        scoreMenu.setTitle("我的成绩");
        scoreMenu.setUserTypeIds("2");
        scoreMenu.setPid(welcomeMenu.getId());
        menuInfoRepository.save(scoreMenu);

        // 创建退出菜单
        MenuInfo logoutMenu = new MenuInfo();
        logoutMenu.setId(++maxId);
        logoutMenu.setName("logout");
        logoutMenu.setTitle("退出");
        logoutMenu.setUserTypeIds("2");
        logoutMenu.setPid(welcomeMenu.getId());
        menuInfoRepository.save(logoutMenu);
    }

    /**
     * 初始化教师角色菜单
     * 如果教师菜单不存在，则创建
     */
    private void initTeacherMenu() {
        // 检查是否已经有教师菜单
        List<MenuInfo> existingMenus = menuInfoRepository.findByUserTypeIds("3");
        for (MenuInfo menu : existingMenus) {
            if (menu.getName() != null && menu.getName().equalsIgnoreCase("WelcomeTeacherPanel")) {
                return; // 菜单已存在，不需要初始化
            }
        }

        // 获取最大菜单ID
        Integer maxId = 100;
        List<MenuInfo> allMenus = menuInfoRepository.findAll();
        for (MenuInfo menu : allMenus) {
            if (menu.getId() != null && menu.getId() > maxId) {
                maxId = menu.getId();
            }
        }

        // 创建教师欢迎页面菜单
        MenuInfo welcomeMenu = new MenuInfo();
        welcomeMenu.setId(++maxId);
        welcomeMenu.setName("WelcomeTeacherPanel");
        welcomeMenu.setTitle("首页");
        welcomeMenu.setUserTypeIds("3"); // 3 = TEACHER角色
        welcomeMenu.setPid(null);
        menuInfoRepository.save(welcomeMenu);

        // 创建我的课程菜单
        MenuInfo courseMenu = new MenuInfo();
        courseMenu.setId(++maxId);
        courseMenu.setName("TeacherCoursePanel");
        courseMenu.setTitle("我的课程");
        courseMenu.setUserTypeIds("3");
        courseMenu.setPid(welcomeMenu.getId());
        menuInfoRepository.save(courseMenu);

        // 创建成绩录入菜单
        MenuInfo scoreMenu = new MenuInfo();
        scoreMenu.setId(++maxId);
        scoreMenu.setName("TeacherScorePanel");
        scoreMenu.setTitle("成绩录入");
        scoreMenu.setUserTypeIds("3");
        scoreMenu.setPid(welcomeMenu.getId());
        menuInfoRepository.save(scoreMenu);

        // 创建考勤管理菜单
        MenuInfo attendanceMenu = new MenuInfo();
        attendanceMenu.setId(++maxId);
        attendanceMenu.setName("TeacherAttendancePanel");
        attendanceMenu.setTitle("考勤管理");
        attendanceMenu.setUserTypeIds("3");
        attendanceMenu.setPid(welcomeMenu.getId());
        menuInfoRepository.save(attendanceMenu);

        // 创建退出菜单
        MenuInfo logoutMenu = new MenuInfo();
        logoutMenu.setId(++maxId);
        logoutMenu.setName("logout");
        logoutMenu.setTitle("退出");
        logoutMenu.setUserTypeIds("3");
        logoutMenu.setPid(welcomeMenu.getId());
        menuInfoRepository.save(logoutMenu);
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

