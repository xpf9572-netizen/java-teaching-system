package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Enrollment;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.EnrollmentRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduleService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public ScheduleService(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }

    public DataResponse getStudentSchedule(Integer studentId) {
        if (studentId == null || studentId <= 0) {
            return CommonMethod.getReturnMessageError("学生ID不能为空");
        }

        Optional<Student> studentOp = studentRepository.findById(studentId);
        if (studentOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }

        Student student = studentOp.get();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentPersonId(studentId);

        List<Map<String, Object>> dataList = new ArrayList<>();
        Set<String> semesterSet = new TreeSet<>();
        semesterSet.add("全部学期");

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            if (course == null) continue;

            Map<String, Object> m = new HashMap<>();
            m.put("enrollmentId", enrollment.getEnrollmentId());
            m.put("courseId", course.getCourseId());
            m.put("courseNum", course.getNum());
            m.put("courseName", course.getName());
            m.put("credit", course.getCredit());
            m.put("semester", enrollment.getSemester());
            m.put("status", enrollment.getStatus());
            m.put("score", enrollment.getScore());

            // Teacher info not available - course table has no teacher_id column
            m.put("teacherId", null);
            m.put("teacherName", "");

            // Course location (from coursePath field or default)
            m.put("location", course.getCoursePath() != null ? course.getCoursePath() : "");

            // Since database doesn't have day-of-week and class-period fields,
            // we use default values. In a real system, this would come from
            // a course_schedule or similar table.
            m.put("dayOfWeek", "");
            m.put("classPeriod", "");

            dataList.add(m);

            if (enrollment.getSemester() != null) {
                semesterSet.add(enrollment.getSemester());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("scheduleList", dataList);
        result.put("semesterList", new ArrayList<>(semesterSet));
        result.put("studentName", student.getPerson() != null ? student.getPerson().getName() : "");
        result.put("studentNum", student.getPerson() != null ? student.getPerson().getNum() : "");

        return CommonMethod.getReturnData(result);
    }

    public DataResponse getScheduleBySemester(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        String semester = dataRequest.getString("semester");

        if (studentId == null || studentId <= 0) {
            studentId = CommonMethod.getPersonId();
        }

        if (studentId == null) {
            return CommonMethod.getReturnMessageError("用户未登录");
        }

        Optional<Student> studentOp = studentRepository.findById(studentId);
        if (studentOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }

        Student student = studentOp.get();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentPersonId(studentId);

        List<Map<String, Object>> dataList = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            // Filter by semester if specified
            if (semester != null && !semester.isEmpty() && !"全部学期".equals(semester)) {
                if (!semester.equals(enrollment.getSemester())) {
                    continue;
                }
            }

            Course course = enrollment.getCourse();
            if (course == null) continue;

            Map<String, Object> m = new HashMap<>();
            m.put("enrollmentId", enrollment.getEnrollmentId());
            m.put("courseId", course.getCourseId());
            m.put("courseNum", course.getNum());
            m.put("courseName", course.getName());
            m.put("credit", course.getCredit());
            m.put("semester", enrollment.getSemester());
            m.put("status", enrollment.getStatus());
            m.put("score", enrollment.getScore());

            // Teacher info not available - course table has no teacher_id column
            m.put("teacherId", null);
            m.put("teacherName", "");

            m.put("location", course.getCoursePath() != null ? course.getCoursePath() : "");
            m.put("dayOfWeek", "");
            m.put("classPeriod", "");

            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }
}
