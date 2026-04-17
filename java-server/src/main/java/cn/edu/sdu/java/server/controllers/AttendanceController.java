package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Attendance;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.repositorys.AttendanceRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
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
@RequestMapping("/api/attendances")
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
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
