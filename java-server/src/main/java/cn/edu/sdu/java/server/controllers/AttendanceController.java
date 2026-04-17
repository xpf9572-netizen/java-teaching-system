package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Attendance;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.AttendanceRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    /**
     * 获取课程学生的考勤列表
     * 前端调用: /api/attendance/getAttendanceList
     */
    @PostMapping("/getAttendanceList")
    public DataResponse getAttendanceList(@RequestBody DataRequest request) {
        Integer courseId = request.getInteger("courseId");
        String attendanceDate = request.getString("attendanceDate");
        String semester = request.getString("semester");

        if (courseId == null || courseId == 0) {
            return CommonMethod.getReturnMessageError("课程ID不能为空");
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 获取选了该课程的学生列表
        List<Student> students = studentRepository.findStudentsByCourseIdAndSemester(courseId, semester != null ? semester : "2024-1");

        for (Student student : students) {
            Map<String, Object> m = new HashMap<>();
            m.put("studentId", student.getPersonId());
            m.put("studentNum", student.getPerson() != null ? student.getPerson().getNum() : "");
            m.put("studentName", student.getPerson() != null ? student.getPerson().getName() : "");
            m.put("className", student.getClassName() != null ? student.getClassName() : "");

            // 查找该学生的考勤记录
            Optional<Attendance> attendanceOp = attendanceRepository.findByStudentAndCourseAndDate(
                    student.getPersonId(), courseId, attendanceDate);
            if (attendanceOp.isPresent()) {
                Attendance a = attendanceOp.get();
                m.put("status", a.getStatus());
                m.put("remark", a.getRemark());
            } else {
                m.put("status", "PRESENT"); // 默认出勤
                m.put("remark", "");
            }
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 保存考勤记录
     * 前端调用: /api/attendance/saveAttendance
     */
    @PostMapping("/saveAttendance")
    public DataResponse saveAttendance(@RequestBody DataRequest request) {
        Integer courseId = request.getInteger("courseId");
        Integer studentId = request.getInteger("studentId");
        String attendanceDate = request.getString("attendanceDate");
        String status = request.getString("status");
        String remark = request.getString("remark");

        if (courseId == null || studentId == null || attendanceDate == null) {
            return CommonMethod.getReturnMessageError("课程ID、学生ID和考勤日期不能为空");
        }

        // 查找是否已存在考勤记录
        Optional<Attendance> existingOp = attendanceRepository.findByStudentAndCourseAndDate(studentId, courseId, attendanceDate);
        Attendance attendance;
        if (existingOp.isPresent()) {
            attendance = existingOp.get();
        } else {
            attendance = new Attendance();
            // 设置学生和课程
            studentRepository.findById(studentId).ifPresent(attendance::setStudent);
            courseRepository.findById(courseId).ifPresent(attendance::setCourse);
            // 设置考勤日期
            try {
                java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(attendanceDate);
                attendance.setAttendanceDate(date);
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("日期格式错误");
            }
        }

        attendance.setStatus(status);
        attendance.setRemark(remark);
        attendanceRepository.save(attendance);

        return CommonMethod.getReturnMessageOK();
    }

    // ============ 原有端点保持兼容 ============

    @GetMapping("/all")
    public Map<String, Object> getAttendances(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "attendanceId"));
        Page<Attendance> attendancePage = attendanceRepository.findAll(pageable);

        List<Map<String, Object>> content = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Attendance a : attendancePage.getContent()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getAttendanceId());
            Student s = a.getStudent();
            Course c = a.getCourse();
            if (s != null) {
                m.put("studentId", s.getPersonId());
                m.put("studentName", s.getPerson() != null ? s.getPerson().getName() : "");
            }
            if (c != null) {
                m.put("courseId", c.getCourseId());
                m.put("courseName", c.getName());
            }
            m.put("attendanceDate", a.getAttendanceDate() != null ? sdf.format(a.getAttendanceDate()) : "");
            m.put("status", a.getStatus());
            m.put("remark", a.getRemark());
            content.add(m);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("totalElements", attendancePage.getTotalElements());
        result.put("totalPages", attendancePage.getTotalPages());
        result.put("content", content);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getAttendance(@PathVariable Integer id) {
        Optional<Attendance> op = attendanceRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (op.isPresent()) {
            Attendance a = op.get();
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getAttendanceId());
            Student s = a.getStudent();
            Course c = a.getCourse();
            if (s != null) {
                m.put("studentId", s.getPersonId());
                m.put("studentName", s.getPerson() != null ? s.getPerson().getName() : "");
            }
            if (c != null) {
                m.put("courseId", c.getCourseId());
                m.put("courseName", c.getName());
            }
            m.put("attendanceDate", a.getAttendanceDate() != null ? sdf.format(a.getAttendanceDate()) : "");
            m.put("status", a.getStatus());
            m.put("remark", a.getRemark());
            result.put("success", true);
            result.put("data", m);
        } else {
            result.put("success", false);
            result.put("msg", "考勤记录不存在");
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> createAttendance(@RequestBody Map<String, Object> data) {
        Attendance attendance = new Attendance();

        if (data.get("studentId") != null) {
            Integer studentId = ((Number) data.get("studentId")).intValue();
            studentRepository.findById(studentId).ifPresent(attendance::setStudent);
        }
        if (data.get("courseId") != null) {
            Integer courseId = ((Number) data.get("courseId")).intValue();
            courseRepository.findById(courseId).ifPresent(attendance::setCourse);
        }
        if (data.get("attendanceDate") != null) {
            try {
                java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse((String) data.get("attendanceDate"));
                attendance.setAttendanceDate(date);
            } catch (Exception e) {}
        }
        if (data.get("status") != null) attendance.setStatus((String) data.get("status"));
        if (data.get("remark") != null) attendance.setRemark((String) data.get("remark"));

        attendanceRepository.save(attendance);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", attendance);
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateAttendance(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        Optional<Attendance> op = attendanceRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            Attendance attendance = op.get();

            if (data.get("studentId") != null) {
                Integer studentId = ((Number) data.get("studentId")).intValue();
                studentRepository.findById(studentId).ifPresent(attendance::setStudent);
            }
            if (data.get("courseId") != null) {
                Integer courseId = ((Number) data.get("courseId")).intValue();
                courseRepository.findById(courseId).ifPresent(attendance::setCourse);
            }
            if (data.get("attendanceDate") != null) {
                try {
                    java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse((String) data.get("attendanceDate"));
                    attendance.setAttendanceDate(date);
                } catch (Exception e) {}
            }
            if (data.get("status") != null) attendance.setStatus((String) data.get("status"));
            if (data.get("remark") != null) attendance.setRemark((String) data.get("remark"));

            attendanceRepository.save(attendance);
            result.put("success", true);
            result.put("data", attendance);
        } else {
            result.put("success", false);
            result.put("msg", "考勤记录不存在");
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteAttendance(@PathVariable Integer id) {
        Optional<Attendance> op = attendanceRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            attendanceRepository.delete(op.get());
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("msg", "考勤记录不存在");
        }
        return result;
    }
}
