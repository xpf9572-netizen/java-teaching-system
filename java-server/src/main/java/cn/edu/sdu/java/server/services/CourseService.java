package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.EnrollmentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public DataResponse getCourseList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit()+"");
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                m.put("preCourseId",pc.getCourseId());
            }
            // 添加选课人数和学期
            Long studentCount = enrollmentRepository.countByCourseIdAndSemester(c.getCourseId(), "2024-1");
            m.put("studentCount", studentCount != null ? studentCount.intValue() : 0);
            m.put("semester", "2024-1");
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse courseSave(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        Integer preCourseId = dataRequest.getInteger("preCourseId");
        Optional<Course> op;
        Course c= null;

        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent())
                c= op.get();
        }
        if(c== null)
            c = new Course();
        Course pc =null;
        if(preCourseId != null) {
            op = courseRepository.findById(preCourseId);
            if(op.isPresent())
                pc = op.get();
        }
        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        c.setCoursePath(coursePath);
        c.setPreCourse(pc);
        courseRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse courseDelete(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c= null;
        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent()) {
                c = op.get();
                courseRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getAllCourses() {
        List<cn.edu.sdu.java.server.models.Course> courses = courseRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (cn.edu.sdu.java.server.models.Course c : courses) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getCourseId());
            m.put("num", c.getNum());
            m.put("name", c.getName());
            m.put("credit", c.getCredit());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getCourseListByTeacher(DataRequest dataRequest) {
        // 通过enrollment表查询有选课记录的课程
        // 由于course表没有teacher_id字段，通过enrollment表关联：找到有学生选修的课程
        String semester = dataRequest.getString("semester");
        if(semester == null || semester.isEmpty())
            semester = "2024-1";
        List<Course> cList = enrollmentRepository.findCoursesBySemester(semester);
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Course c : cList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId()+"");
            m.put("num", c.getNum());
            m.put("name", c.getName());
            m.put("credit", c.getCredit()+"");
            if(c.getPreCourse() != null) {
                m.put("preCourse", c.getPreCourse().getName());
                m.put("preCourseId", c.getPreCourse().getCourseId());
            }
            Long studentCount = enrollmentRepository.countByCourseIdAndSemester(c.getCourseId(), semester);
            m.put("studentCount", studentCount != null ? studentCount.intValue() : 0);
            m.put("semester", semester);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getCourseOptionList(DataRequest dataRequest) {
        List<Course> courses = courseRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Course c : courses) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getCourseId());
            m.put("value", c.getCourseId().toString());
            m.put("title", c.getNum() + " - " + c.getName());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

}
