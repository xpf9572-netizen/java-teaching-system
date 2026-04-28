package cn.edu.sdu.java.server.services;


import cn.edu.sdu.java.server.models.ClassEntity;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.CourseSchedule;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ClassEntityRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.CourseScheduleRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseScheduleService {
    private final CourseScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ClassEntityRepository classEntityRepository;

    public CourseScheduleService(CourseScheduleRepository scheduleRepository,
                                 CourseRepository courseRepository,
                                 TeacherRepository teacherRepository,
                                 ClassEntityRepository classEntityRepository) {
        this.scheduleRepository = scheduleRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.classEntityRepository = classEntityRepository;
    }

    public DataResponse getScheduleList(DataRequest dataRequest) {
        List<CourseSchedule> schedules;
        String semester = dataRequest.getString("semester");

        if (semester != null && !semester.isEmpty()) {
            schedules = scheduleRepository.findBySemester(semester);
        } else {
            schedules = scheduleRepository.findAll();
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (CourseSchedule cs : schedules) {
            dataList.add(scheduleToMap(cs));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse scheduleSave(DataRequest dataRequest) {
        Integer scheduleId = dataRequest.getInteger("scheduleId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer classId = dataRequest.getInteger("classId");
        String classroom = dataRequest.getString("classroom");
        String dayOfWeek = dataRequest.getString("dayOfWeek");
        String classPeriod = dataRequest.getString("classPeriod");
        String weekRange = dataRequest.getString("weekRange");
        String semester = dataRequest.getString("semester");
        String remark = dataRequest.getString("remark");

        if (courseId == null || teacherId == null || classId == null ||
            classroom == null || dayOfWeek == null || classPeriod == null || semester == null) {
            return CommonMethod.getReturnMessageError("缺少必要参数");
        }

        // Validate entities exist
        Optional<Course> courseOp = courseRepository.findById(courseId);
        if (courseOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程不存在");
        }

        Optional<Teacher> teacherOp = teacherRepository.findById(teacherId);
        if (teacherOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("教师不存在");
        }

        Optional<ClassEntity> classOp = classEntityRepository.findById(classId);
        if (classOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("班级不存在");
        }

        // Check for conflicts
        String conflictMsg = checkConflicts(scheduleId, teacherId, classroom, semester, dayOfWeek, classPeriod, classId);
        if (conflictMsg != null) {
            return CommonMethod.getReturnMessageError(conflictMsg);
        }

        CourseSchedule schedule;
        if (scheduleId != null && scheduleId > 0) {
            Optional<CourseSchedule> op = scheduleRepository.findById(scheduleId);
            if (op.isPresent()) {
                schedule = op.get();
            } else {
                return CommonMethod.getReturnMessageError("课程安排不存在");
            }
        } else {
            schedule = new CourseSchedule();
        }

        schedule.setCourse(courseOp.get());
        schedule.setTeacher(teacherOp.get());
        schedule.setClassEntity(classOp.get());
        schedule.setClassroom(classroom);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setClassPeriod(classPeriod);
        schedule.setWeekRange(weekRange);
        schedule.setSemester(semester);
        schedule.setRemark(remark);

        scheduleRepository.save(schedule);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse scheduleDelete(DataRequest dataRequest) {
        Integer scheduleId = dataRequest.getInteger("scheduleId");
        if (scheduleId == null) {
            return CommonMethod.getReturnMessageError("scheduleId不能为空");
        }

        Optional<CourseSchedule> op = scheduleRepository.findById(scheduleId);
        if (op.isPresent()) {
            CommonMethod.logDeleteOperation("course_schedule", scheduleId);
            scheduleRepository.delete(op.get());
            return CommonMethod.getReturnMessageOK();
        } else {
            return CommonMethod.getReturnMessageError("课程安排不存在");
        }
    }

    public DataResponse getScheduleByTeacher(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        if (teacherId == null) {
            teacherId = CommonMethod.getPersonId();
        }

        List<CourseSchedule> schedules = scheduleRepository.findByTeacherPersonId(teacherId);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (CourseSchedule cs : schedules) {
            dataList.add(scheduleToMap(cs));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getScheduleByClass(DataRequest dataRequest) {
        Integer classId = dataRequest.getInteger("classId");
        if (classId == null) {
            return CommonMethod.getReturnMessageError("classId不能为空");
        }

        List<CourseSchedule> schedules = scheduleRepository.findByClassEntityClassId(classId);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (CourseSchedule cs : schedules) {
            dataList.add(scheduleToMap(cs));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getScheduleByCourse(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null) {
            return CommonMethod.getReturnMessageError("courseId不能为空");
        }

        List<CourseSchedule> schedules = scheduleRepository.findByCourseCourseId(courseId);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (CourseSchedule cs : schedules) {
            dataList.add(scheduleToMap(cs));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getScheduleOptionList(DataRequest dataRequest) {
        List<CourseSchedule> schedules = scheduleRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (CourseSchedule cs : schedules) {
            Map<String, Object> m = new HashMap<>();
            m.put("scheduleId", cs.getScheduleId());
            m.put("courseName", cs.getCourse() != null ? cs.getCourse().getName() : "");
            m.put("teacherName", cs.getTeacher() != null && cs.getTeacher().getPerson() != null ? cs.getTeacher().getPerson().getName() : "");
            m.put("className", cs.getClassEntity() != null ? cs.getClassEntity().getClassName() : "");
            m.put("semester", cs.getSemester());
            m.put("dayOfWeek", cs.getDayOfWeek());
            m.put("classPeriod", cs.getClassPeriod());
            m.put("classroom", cs.getClassroom());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    private String checkConflicts(Integer scheduleId, Integer teacherId, String classroom,
                                   String semester, String dayOfWeek, String classPeriod, Integer classId) {
        // Check teacher conflict
        List<CourseSchedule> teacherConflicts = scheduleRepository.findTeacherConflict(teacherId, semester, dayOfWeek, classPeriod);
        for (CourseSchedule cs : teacherConflicts) {
            if (scheduleId == null || !cs.getScheduleId().equals(scheduleId)) {
                String teacherName = cs.getTeacher() != null && cs.getTeacher().getPerson() != null ?
                    cs.getTeacher().getPerson().getName() : "未知教师";
                return "教师冲突：该教师在" + semester + " " + dayOfWeek + " 第" + classPeriod + "节已有课程: " +
                       (cs.getCourse() != null ? cs.getCourse().getName() : "") + " (" + teacherName + ")";
            }
        }

        // Check classroom conflict
        List<CourseSchedule> classroomConflicts = scheduleRepository.findClassroomConflict(classroom, semester, dayOfWeek, classPeriod);
        for (CourseSchedule cs : classroomConflicts) {
            if (scheduleId == null || !cs.getScheduleId().equals(scheduleId)) {
                String conflictClassName = cs.getClassEntity() != null ? cs.getClassEntity().getClassName() : "未知班级";
                return "教室冲突：教室" + classroom + "在" + semester + " " + dayOfWeek + " 第" + classPeriod + "节已被班级 " + conflictClassName + " 使用";
            }
        }

        // Check class conflict
        List<CourseSchedule> classConflicts = scheduleRepository.findClassConflict(classId, semester, dayOfWeek, classPeriod);
        for (CourseSchedule cs : classConflicts) {
            if (scheduleId == null || !cs.getScheduleId().equals(scheduleId)) {
                String courseName = cs.getCourse() != null ? cs.getCourse().getName() : "未知课程";
                return "班级冲突：班级在" + semester + " " + dayOfWeek + " 第" + classPeriod + "节已有课程: " + courseName;
            }
        }

        return null;
    }

    private Map<String, Object> scheduleToMap(CourseSchedule cs) {
        Map<String, Object> m = new HashMap<>();
        m.put("scheduleId", cs.getScheduleId());
        m.put("courseId", cs.getCourse() != null ? cs.getCourse().getCourseId() : null);
        m.put("courseNum", cs.getCourse() != null ? cs.getCourse().getNum() : "");
        m.put("courseName", cs.getCourse() != null ? cs.getCourse().getName() : "");
        m.put("teacherId", cs.getTeacher() != null ? cs.getTeacher().getPersonId() : null);
        m.put("teacherName", cs.getTeacher() != null && cs.getTeacher().getPerson() != null ? cs.getTeacher().getPerson().getName() : "");
        m.put("classId", cs.getClassEntity() != null ? cs.getClassEntity().getClassId() : null);
        m.put("className", cs.getClassEntity() != null ? cs.getClassEntity().getClassName() : "");
        m.put("classroom", cs.getClassroom());
        m.put("dayOfWeek", cs.getDayOfWeek());
        m.put("classPeriod", cs.getClassPeriod());
        m.put("weekRange", cs.getWeekRange());
        m.put("semester", cs.getSemester());
        m.put("remark", cs.getRemark());
        return m;
    }

    public DataResponse getCourseList() {
        try {
            System.out.println("=== Service: getCourseList 开始 ===");
            List<Course> courses = courseRepository.findAll();
            System.out.println("查到课程数量: " + courses.size());
            DataResponse response = new DataResponse();
            response.setCode(0);
            response.setData(courses);
            response.setMsg("成功");
            return response;
        } catch (Exception e) {
            System.out.println("=== Service: getCourseList 报错 ===");
            e.printStackTrace();
            DataResponse errorResponse = new DataResponse();
            errorResponse.setCode(1);
            errorResponse.setData(null);
            errorResponse.setMsg("服务器内部错误: " + e.toString());
            return errorResponse;
        }
    }

    public DataResponse getTeacherList() {
        try {
            System.out.println("=== getTeacherList 开始 ===");
            List<Teacher> teachers = teacherRepository.findAll();
            System.out.println("查到教师数量: " + teachers.size());
            DataResponse response = new DataResponse();
            response.setCode(0);
            response.setData(teachers);
            response.setMsg("成功");
            return response;
        } catch (Exception e) {
            System.out.println("=== getTeacherList 报错 ===");
            e.printStackTrace();
            DataResponse errorResponse = new DataResponse();
            errorResponse.setCode(1);
            errorResponse.setData(null);
            errorResponse.setMsg("服务器内部错误: " + e.toString());
            return errorResponse;
        }
    }
}
