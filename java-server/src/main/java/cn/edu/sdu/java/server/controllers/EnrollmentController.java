package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Enrollment;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.EnrollmentRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentController(EnrollmentRepository enrollmentRepository,
                                StudentRepository studentRepository,
                                CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * 获取当前学生的选课列表
     */
    @PostMapping("/my")
    public DataResponse getMyEnrollments(@RequestBody DataRequest request) {
        Integer studentId = request.getInteger("studentId");
        if (studentId == null) {
            studentId = CommonMethod.getPersonId();
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentPersonId(studentId);
        List<Map<String, Object>> dataList = new ArrayList<>();

        for (Enrollment e : enrollments) {
            Map<String, Object> m = new HashMap<>();
            m.put("enrollmentId", e.getEnrollmentId());
            m.put("courseId", e.getCourse() != null ? e.getCourse().getCourseId() : null);
            m.put("courseName", e.getCourse() != null ? e.getCourse().getName() : "");
            m.put("courseNum", e.getCourse() != null ? e.getCourse().getNum() : "");
            m.put("credit", e.getCourse() != null ? e.getCourse().getCredit() : null);
            m.put("semester", e.getSemester());
            m.put("score", e.getScore());
            m.put("status", e.getStatus());
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 保存选课（新增或更新）
     */
    @PostMapping("/save")
    public DataResponse saveEnrollment(@RequestBody DataRequest request) {
        Integer enrollmentId = request.getInteger("enrollmentId");
        Integer studentId = request.getInteger("studentId");
        Integer courseId = request.getInteger("courseId");
        String semester = request.getString("semester");
        Double score = request.getDouble("score");

        if (studentId == null || courseId == null) {
            return CommonMethod.getReturnMessageError("学生ID和课程ID不能为空");
        }

        Enrollment enrollment = null;
        if (enrollmentId != null && enrollmentId > 0) {
            Optional<Enrollment> op = enrollmentRepository.findById(enrollmentId);
            if (op.isPresent()) {
                enrollment = op.get();
            }
        }

        if (enrollment == null) {
            // 检查是否重复选课
            Optional<Enrollment> existing = enrollmentRepository.findByStudentAndCourseAndSemester(studentId, courseId, semester);
            if (existing.isPresent()) {
                return CommonMethod.getReturnMessageError("该学生已在相同学期选过此课程，不能重复选课！");
            }
            enrollment = new Enrollment();
        }

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }
        enrollment.setStudent(studentOpt.get());

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程不存在");
        }
        enrollment.setCourse(courseOpt.get());

        if (semester != null) enrollment.setSemester(semester);
        if (score != null) enrollment.setScore(score);
        if (enrollment.getStatus() == null) enrollment.setStatus("选课中");

        enrollmentRepository.save(enrollment);
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 删除选课
     */
    @PostMapping("/delete")
    public DataResponse deleteEnrollment(@RequestBody DataRequest request) {
        Integer enrollmentId = request.getInteger("enrollmentId");
        if (enrollmentId == null || enrollmentId <= 0) {
            return CommonMethod.getReturnMessageError("选课ID不能为空");
        }

        Optional<Enrollment> op = enrollmentRepository.findById(enrollmentId);
        if (op.isEmpty()) {
            return CommonMethod.getReturnMessageError("选课记录不存在");
        }

        enrollmentRepository.delete(op.get());
        return CommonMethod.getReturnMessageOK();
    }

    @GetMapping
    public Map<String, Object> getEnrollments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String studentName) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "enrollmentId"));
        Page<Enrollment> enrollmentPage = enrollmentRepository.findAll(pageable);

        List<Map<String, Object>> content = new ArrayList<>();
        for (Enrollment e : enrollmentPage.getContent()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getEnrollmentId());
            Student s = e.getStudent();
            Course c = e.getCourse();
            if (s != null) {
                m.put("studentId", s.getPersonId());
                m.put("studentName", s.getPerson() != null ? s.getPerson().getName() : "");
            }
            if (c != null) {
                m.put("courseId", c.getCourseId());
                m.put("courseName", c.getName());
            }
            m.put("score", e.getScore());
            m.put("semester", e.getSemester());
            m.put("status", e.getStatus());
            content.add(m);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("totalElements", enrollmentPage.getTotalElements());
        result.put("totalPages", enrollmentPage.getTotalPages());
        result.put("content", content);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getEnrollment(@PathVariable Integer id) {
        Optional<Enrollment> op = enrollmentRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            Enrollment e = op.get();
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getEnrollmentId());
            Student s = e.getStudent();
            Course c = e.getCourse();
            if (s != null) {
                m.put("studentId", s.getPersonId());
                m.put("studentName", s.getPerson() != null ? s.getPerson().getName() : "");
            }
            if (c != null) {
                m.put("courseId", c.getCourseId());
                m.put("courseName", c.getName());
            }
            m.put("score", e.getScore());
            m.put("semester", e.getSemester());
            m.put("status", e.getStatus());
            result.put("success", true);
            result.put("data", m);
        } else {
            result.put("success", false);
            result.put("msg", "选课记录不存在");
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> createEnrollment(@RequestBody Map<String, Object> data) {
        Integer studentId = null;
        Integer courseId = null;
        String semester = null;

        if (data.get("studentId") != null) {
            studentId = ((Number) data.get("studentId")).intValue();
        }
        if (data.get("courseId") != null) {
            courseId = ((Number) data.get("courseId")).intValue();
        }
        if (data.get("semester") != null) {
            semester = (String) data.get("semester");
        }

        // 检查选课冲突
        if (studentId != null && courseId != null && semester != null) {
            Optional<Enrollment> existing = enrollmentRepository.findByStudentAndCourseAndSemester(studentId, courseId, semester);
            if (existing.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("msg", "该学生已在相同学期选过此课程，不能重复选课！");
                return result;
            }
        }

        Enrollment enrollment = new Enrollment();

        if (studentId != null) {
            studentRepository.findById(studentId).ifPresent(enrollment::setStudent);
        }
        if (courseId != null) {
            courseRepository.findById(courseId).ifPresent(enrollment::setCourse);
        }
        if (data.get("score") != null) enrollment.setScore(((Number) data.get("score")).doubleValue());
        if (semester != null) enrollment.setSemester(semester);
        if (data.get("status") != null) enrollment.setStatus((String) data.get("status"));

        enrollmentRepository.save(enrollment);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        Map<String, Object> enrollmentData = new HashMap<>();
        enrollmentData.put("id", enrollment.getEnrollmentId());
        enrollmentData.put("studentId", enrollment.getStudent() != null ? enrollment.getStudent().getPersonId() : null);
        enrollmentData.put("courseId", enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null);
        enrollmentData.put("score", enrollment.getScore());
        enrollmentData.put("semester", enrollment.getSemester());
        enrollmentData.put("status", enrollment.getStatus());
        result.put("data", enrollmentData);
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateEnrollment(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        Optional<Enrollment> op = enrollmentRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            Enrollment enrollment = op.get();

            if (data.get("studentId") != null) {
                Integer studentId = ((Number) data.get("studentId")).intValue();
                studentRepository.findById(studentId).ifPresent(enrollment::setStudent);
            }
            if (data.get("courseId") != null) {
                Integer courseId = ((Number) data.get("courseId")).intValue();
                courseRepository.findById(courseId).ifPresent(enrollment::setCourse);
            }
            if (data.get("score") != null) enrollment.setScore(((Number) data.get("score")).doubleValue());
            if (data.get("semester") != null) enrollment.setSemester((String) data.get("semester"));
            if (data.get("status") != null) enrollment.setStatus((String) data.get("status"));

            enrollmentRepository.save(enrollment);
            result.put("success", true);
            Map<String, Object> enrollmentData = new HashMap<>();
            enrollmentData.put("id", enrollment.getEnrollmentId());
            enrollmentData.put("studentId", enrollment.getStudent() != null ? enrollment.getStudent().getPersonId() : null);
            enrollmentData.put("courseId", enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null);
            enrollmentData.put("score", enrollment.getScore());
            enrollmentData.put("semester", enrollment.getSemester());
            enrollmentData.put("status", enrollment.getStatus());
            result.put("data", enrollmentData);
        } else {
            result.put("success", false);
            result.put("msg", "选课记录不存在");
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteEnrollment(@PathVariable Integer id) {
        Optional<Enrollment> op = enrollmentRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            enrollmentRepository.delete(op.get());
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("msg", "选课记录不存在");
        }
        return result;
    }
}
