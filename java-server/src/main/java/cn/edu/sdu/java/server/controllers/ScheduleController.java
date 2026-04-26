package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ScheduleService;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or (hasRole('STUDENT') and #studentId == T(cn.edu.sdu.java.server.util.CommonMethod).getPersonId())")
    public DataResponse getStudentSchedule(@PathVariable Integer studentId) {
        return scheduleService.getStudentSchedule(studentId);
    }

    @PostMapping("/getStudentSchedule")
    @PreAuthorize("isAuthenticated()")
    public DataResponse getStudentSchedulePost(@RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        String roleName = CommonMethod.getRoleName();

        // 学生只能查看自己的课表
        if ("ROLE_STUDENT".equals(roleName)) {
            Integer currentPersonId = CommonMethod.getPersonId();
            if (studentId == null || !studentId.equals(currentPersonId)) {
                studentId = currentPersonId; // 使用当前登录用户的ID
            }
        } else if (studentId == null || studentId <= 0) {
            studentId = CommonMethod.getPersonId();
        }

        return scheduleService.getStudentSchedule(studentId);
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public DataResponse getCurrentStudentSchedule() {
        Integer studentId = CommonMethod.getPersonId();
        if (studentId == null) {
            return CommonMethod.getReturnMessageError("用户未登录");
        }
        return scheduleService.getStudentSchedule(studentId);
    }

    @PostMapping("/getScheduleBySemester")
    @PreAuthorize("isAuthenticated()")
    public DataResponse getScheduleBySemester(@RequestBody DataRequest dataRequest) {
        return scheduleService.getScheduleBySemester(dataRequest);
    }
}
