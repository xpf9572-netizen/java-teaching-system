package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Enrollment;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.EnrollmentRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public DataResponse getEnrollmentList(DataRequest dataRequest) {
        String studentName = dataRequest.getString("studentName");
        if (studentName == null) studentName = "";
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentListByStudentName(studentName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Enrollment e : enrollments) {
            m = new HashMap<>();
            m.put("id", e.getEnrollmentId());
            Student s = e.getStudent();
            Course c = e.getCourse();
            if (s != null) {
                m.put("studentId", s.getPersonId());
                m.put("studentName", s.getPerson().getName());
            }
            if (c != null) {
                m.put("courseId", c.getCourseId());
                m.put("courseName", c.getName());
            }
            m.put("score", e.getScore());
            m.put("semester", e.getSemester());
            m.put("status", e.getStatus());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @Transactional
    public DataResponse enrollmentSave(DataRequest dataRequest) {
        Integer enrollmentId = dataRequest.getInteger("enrollmentId");
        Long studentId = dataRequest.getLong("studentId");
        Long courseId = dataRequest.getLong("courseId");
        Double score = dataRequest.getDouble("score");
        String semester = dataRequest.getString("semester");
        String status = dataRequest.getString("status");

        Optional<Enrollment> op;
        Enrollment enrollment = null;

        if (enrollmentId != null) {
            op = enrollmentRepository.findById(enrollmentId);
            if (op.isPresent()) {
                enrollment = op.get();
            }
        }

        if (enrollment == null) {
            enrollment = new Enrollment();
        }

        if (studentId != null) {
            Optional<Student> studentOp = studentRepository.findById(studentId.intValue());
            studentOp.ifPresent(enrollment::setStudent);
        }

        if (courseId != null) {
            Optional<Course> courseOp = courseRepository.findById(courseId.intValue());
            courseOp.ifPresent(enrollment::setCourse);
        }

        enrollment.setScore(score);
        enrollment.setSemester(semester);
        enrollment.setStatus(status);
        enrollmentRepository.save(enrollment);

        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse enrollmentDelete(DataRequest dataRequest) {
        Integer enrollmentId = dataRequest.getInteger("enrollmentId");
        if (enrollmentId != null) {
            Optional<Enrollment> op = enrollmentRepository.findById(enrollmentId);
            if (op.isPresent()) {
                enrollmentRepository.delete(op.get());
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
