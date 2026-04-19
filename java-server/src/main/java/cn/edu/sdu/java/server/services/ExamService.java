package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Exam;
import cn.edu.sdu.java.server.models.ExamViolation;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.ExamRepository;
import cn.edu.sdu.java.server.repositorys.ExamViolationRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamViolationRepository violationRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public ExamService(ExamRepository examRepository,
                      ExamViolationRepository violationRepository,
                      CourseRepository courseRepository,
                      TeacherRepository teacherRepository) {
        this.examRepository = examRepository;
        this.violationRepository = violationRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    public DataResponse getExamList(DataRequest dataRequest) {
        String semester = dataRequest.getString("semester");
        List<Exam> exams;

        if (semester != null && !semester.isEmpty()) {
            exams = examRepository.findBySemester(semester);
        } else {
            exams = examRepository.findAll();
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Exam exam : exams) {
            dataList.add(examToMap(exam));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse examSave(DataRequest dataRequest) {
        Integer examId = dataRequest.getInteger("examId");
        Integer courseId = dataRequest.getInteger("courseId");
        String semester = dataRequest.getString("semester");
        Date examDate = dataRequest.getDate("examDate");
        String examTime = dataRequest.getString("examTime");
        String examLocation = dataRequest.getString("examLocation");
        Integer invigilatorId = dataRequest.getInteger("invigilatorId");
        String examType = dataRequest.getString("examType");
        Integer totalStudents = dataRequest.getInteger("totalStudents");
        String remark = dataRequest.getString("remark");

        if (courseId == null || semester == null || examDate == null || examTime == null || examLocation == null) {
            return CommonMethod.getReturnMessageError("缺少必要参数");
        }

        Optional<Course> courseOp = courseRepository.findById(courseId);
        if (courseOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程不存在");
        }

        Exam exam;
        if (examId != null && examId > 0) {
            Optional<Exam> op = examRepository.findById(examId);
            if (op.isPresent()) {
                exam = op.get();
            } else {
                return CommonMethod.getReturnMessageError("考试记录不存在");
            }
        } else {
            exam = new Exam();
        }

        exam.setCourse(courseOp.get());
        exam.setSemester(semester);
        exam.setExamDate(examDate);
        exam.setExamTime(examTime);
        exam.setExamLocation(examLocation);
        exam.setExamType(examType != null ? examType : "FINAL");
        exam.setTotalStudents(totalStudents != null ? totalStudents : 0);
        exam.setRemark(remark);

        if (invigilatorId != null) {
            Optional<Teacher> teacherOp = teacherRepository.findById(invigilatorId);
            teacherOp.ifPresent(exam::setInvigilator);
        }

        examRepository.save(exam);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse examDelete(DataRequest dataRequest) {
        Integer examId = dataRequest.getInteger("examId");
        if (examId == null) {
            return CommonMethod.getReturnMessageError("examId不能为空");
        }

        Optional<Exam> op = examRepository.findById(examId);
        if (op.isPresent()) {
            examRepository.delete(op.get());
            return CommonMethod.getReturnMessageOK();
        } else {
            return CommonMethod.getReturnMessageError("考试记录不存在");
        }
    }

    public DataResponse getViolationList(DataRequest dataRequest) {
        Integer examId = dataRequest.getInteger("examId");
        List<ExamViolation> violations;

        if (examId != null && examId > 0) {
            violations = violationRepository.findByExamExamId(examId);
        } else {
            violations = violationRepository.findAll();
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (ExamViolation v : violations) {
            dataList.add(violationToMap(v));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse violationSave(DataRequest dataRequest) {
        Integer violationId = dataRequest.getInteger("violationId");
        Integer examId = dataRequest.getInteger("examId");
        Integer studentId = dataRequest.getInteger("studentId");
        String violationType = dataRequest.getString("violationType");
        String violationDesc = dataRequest.getString("violationDesc");
        String punishment = dataRequest.getString("punishment");
        String remark = dataRequest.getString("remark");

        if (examId == null || studentId == null || violationType == null) {
            return CommonMethod.getReturnMessageError("缺少必要参数");
        }

        Optional<Exam> examOp = examRepository.findById(examId);
        if (examOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("考试记录不存在");
        }

        ExamViolation violation;
        if (violationId != null && violationId > 0) {
            Optional<ExamViolation> op = violationRepository.findById(violationId);
            if (op.isPresent()) {
                violation = op.get();
            } else {
                return CommonMethod.getReturnMessageError("违纪记录不存在");
            }
        } else {
            violation = new ExamViolation();
            violation.setRecordTime(new Date());
            violation.setRecordOperator(CommonMethod.getPersonId());
        }

        violation.setExam(examOp.get());
        cn.edu.sdu.java.server.models.Student student = new cn.edu.sdu.java.server.models.Student();
        student.setPersonId(studentId);
        violation.setStudent(student);
        violation.setViolationType(violationType);
        violation.setViolationDesc(violationDesc);
        violation.setPunishment(punishment);
        violation.setRemark(remark);

        violationRepository.save(violation);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse violationDelete(DataRequest dataRequest) {
        Integer violationId = dataRequest.getInteger("violationId");
        if (violationId == null) {
            return CommonMethod.getReturnMessageError("violationId不能为空");
        }

        Optional<ExamViolation> op = violationRepository.findById(violationId);
        if (op.isPresent()) {
            violationRepository.delete(op.get());
            return CommonMethod.getReturnMessageOK();
        } else {
            return CommonMethod.getReturnMessageError("违纪记录不存在");
        }
    }

    private Map<String, Object> examToMap(Exam exam) {
        Map<String, Object> m = new HashMap<>();
        m.put("examId", exam.getExamId());
        m.put("courseId", exam.getCourse() != null ? exam.getCourse().getCourseId() : null);
        m.put("courseName", exam.getCourse() != null ? exam.getCourse().getName() : "");
        m.put("courseNum", exam.getCourse() != null ? exam.getCourse().getNum() : "");
        m.put("semester", exam.getSemester());
        m.put("examDate", exam.getExamDate());
        m.put("examTime", exam.getExamTime());
        m.put("examLocation", exam.getExamLocation());
        m.put("invigilatorId", exam.getInvigilator() != null ? exam.getInvigilator().getPersonId() : null);
        m.put("invigilatorName", exam.getInvigilator() != null && exam.getInvigilator().getPerson() != null ? exam.getInvigilator().getPerson().getName() : "");
        m.put("examType", exam.getExamType());
        m.put("totalStudents", exam.getTotalStudents());
        m.put("remark", exam.getRemark());
        return m;
    }

    private Map<String, Object> violationToMap(ExamViolation v) {
        Map<String, Object> m = new HashMap<>();
        m.put("violationId", v.getViolationId());
        m.put("examId", v.getExam() != null ? v.getExam().getExamId() : null);
        m.put("examName", v.getExam() != null && v.getExam().getCourse() != null ? v.getExam().getCourse().getName() : "");
        m.put("studentId", v.getStudent() != null ? v.getStudent().getPersonId() : null);
        m.put("studentName", "");
        m.put("violationType", v.getViolationType());
        m.put("violationDesc", v.getViolationDesc());
        m.put("punishment", v.getPunishment());
        m.put("recordTime", v.getRecordTime());
        m.put("remark", v.getRemark());
        return m;
    }
}
