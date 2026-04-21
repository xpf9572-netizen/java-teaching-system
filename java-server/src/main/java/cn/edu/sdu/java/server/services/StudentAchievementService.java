package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentAchievement;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentAchievementRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentAchievementService {
    private final StudentAchievementRepository achievementRepository;
    private final StudentRepository studentRepository;

    public StudentAchievementService(StudentAchievementRepository achievementRepository, StudentRepository studentRepository) {
        this.achievementRepository = achievementRepository;
        this.studentRepository = studentRepository;
    }

    public DataResponse getAchievementList(DataRequest dataRequest) {
        Integer requestPersonId = dataRequest.getInteger("personId");
        String type = dataRequest.getString("type");

        Integer currentPersonId = CommonMethod.getPersonId();
        String roleName = CommonMethod.getRoleName();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(roleName);

        Integer personId;
        if (isAdmin && requestPersonId != null) {
            personId = requestPersonId;
        } else {
            personId = currentPersonId;
        }

        List<StudentAchievement> list;
        if (personId != null && type != null && !type.isEmpty()) {
            list = achievementRepository.findByStudentPersonIdAndType(personId, type);
        } else if (personId != null) {
            list = achievementRepository.findByStudentPersonIdOrderByDateDesc(personId);
        } else {
            list = new ArrayList<>();
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (StudentAchievement sa : list) {
            dataList.add(getMapFromAchievement(sa));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse saveAchievement(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer achievementId = CommonMethod.getInteger(form, "achievementId");
        Integer requestStudentId = CommonMethod.getInteger(form, "studentId");
        String type = CommonMethod.getString(form, "type");
        String name = CommonMethod.getString(form, "name");
        String level = CommonMethod.getString(form, "level");
        String awardDate = CommonMethod.getString(form, "awardDate");
        String description = CommonMethod.getString(form, "description");
        String certificateUrl = CommonMethod.getString(form, "certificateUrl");

        Integer currentPersonId = CommonMethod.getPersonId();
        String roleName = CommonMethod.getRoleName();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(roleName);

        Integer studentId;
        Student student;
        StudentAchievement sa;

        if (achievementId != null && achievementId > 0) {
            Optional<StudentAchievement> op = achievementRepository.findById(achievementId);
            if (op.isEmpty()) {
                return CommonMethod.getReturnMessageError("成就记录不存在");
            }
            sa = op.get();
            student = sa.getStudent();
            if (!isAdmin && !student.getPersonId().equals(currentPersonId)) {
                return CommonMethod.getReturnMessageError("无权限修改他人成就");
            }
            if (type != null) sa.setType(type);
            if (name != null) sa.setName(name);
            if (level != null) sa.setLevel(level);
            if (awardDate != null) sa.setAwardDate(awardDate);
            if (description != null) sa.setDescription(description);
            if (certificateUrl != null) sa.setCertificateUrl(certificateUrl);
            achievementRepository.save(sa);
            return CommonMethod.getReturnMessageOK();
        }

        if (isAdmin) {
            if (requestStudentId == null) {
                return CommonMethod.getReturnMessageError("学生ID不能为空");
            }
            Optional<Student> studentOp = studentRepository.findById(requestStudentId);
            if (studentOp.isEmpty()) {
                return CommonMethod.getReturnMessageError("学生不存在");
            }
            student = studentOp.get();
        } else {
            studentId = currentPersonId;
            Optional<Student> studentOp = studentRepository.findById(studentId);
            if (studentOp.isEmpty()) {
                return CommonMethod.getReturnMessageError("学生不存在");
            }
            student = studentOp.get();
        }

        sa = new StudentAchievement();
        sa.setStudent(student);
        sa.setType(type);
        sa.setName(name);
        sa.setLevel(level);
        sa.setAwardDate(awardDate);
        sa.setDescription(description);
        sa.setCertificateUrl(certificateUrl);
        sa.setStatus("PENDING");

        achievementRepository.save(sa);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse deleteAchievement(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        if (achievementId != null && achievementId > 0) {
            Optional<StudentAchievement> op = achievementRepository.findById(achievementId);
            if (op.isPresent()) {
                StudentAchievement sa = op.get();
                Integer currentPersonId = CommonMethod.getPersonId();
                String roleName = CommonMethod.getRoleName();
                boolean isAdmin = "ADMIN".equalsIgnoreCase(roleName);

                if (!isAdmin && !sa.getStudent().getPersonId().equals(currentPersonId)) {
                    return CommonMethod.getReturnMessageError("无权限删除他人成就");
                }
                CommonMethod.logDeleteOperation("student_achievement", achievementId);
                achievementRepository.delete(sa);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse approveAchievement(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        if (achievementId == null || achievementId <= 0) {
            return CommonMethod.getReturnMessageError("成就ID不能为空");
        }
        Optional<StudentAchievement> op = achievementRepository.findById(achievementId);
        if (op.isEmpty()) {
            return CommonMethod.getReturnMessageError("成就记录不存在");
        }
        StudentAchievement sa = op.get();
        sa.setStatus("APPROVED");
        achievementRepository.save(sa);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse rejectAchievement(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");
        if (achievementId == null || achievementId <= 0) {
            return CommonMethod.getReturnMessageError("成就ID不能为空");
        }
        Optional<StudentAchievement> op = achievementRepository.findById(achievementId);
        if (op.isEmpty()) {
            return CommonMethod.getReturnMessageError("成就记录不存在");
        }
        StudentAchievement sa = op.get();
        sa.setStatus("REJECTED");
        achievementRepository.save(sa);
        return CommonMethod.getReturnMessageOK();
    }

    private Map<String, Object> getMapFromAchievement(StudentAchievement sa) {
        Map<String, Object> m = new HashMap<>();
        m.put("achievementId", sa.getAchievementId());
        if (sa.getStudent() != null) {
            m.put("studentId", sa.getStudent().getPersonId());
            m.put("studentName", sa.getStudent().getPerson() != null ? sa.getStudent().getPerson().getName() : "");
        }
        m.put("type", sa.getType());
        m.put("name", sa.getName());
        m.put("level", sa.getLevel());
        m.put("awardDate", sa.getAwardDate());
        m.put("description", sa.getDescription());
        m.put("certificateUrl", sa.getCertificateUrl());
        m.put("status", sa.getStatus());
        return m;
    }
}
