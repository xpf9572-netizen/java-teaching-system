package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CourseScheduleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courseSchedule")
public class CourseScheduleController {
    private final CourseScheduleService scheduleService;

    public CourseScheduleController(CourseScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getAllSchedules() {
        return scheduleService.getScheduleList(new DataRequest());
    }

    @PostMapping("/getScheduleList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getScheduleList(@Valid @RequestBody DataRequest dataRequest) {
        return scheduleService.getScheduleList(dataRequest);
    }

    @PostMapping("/scheduleSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scheduleSave(@Valid @RequestBody DataRequest dataRequest) {
        return scheduleService.scheduleSave(dataRequest);
    }

    @PostMapping("/scheduleDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scheduleDelete(@Valid @RequestBody DataRequest dataRequest) {
        return scheduleService.scheduleDelete(dataRequest);
    }

    @PostMapping("/getScheduleByTeacher")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getScheduleByTeacher(@Valid @RequestBody DataRequest dataRequest) {
        return scheduleService.getScheduleByTeacher(dataRequest);
    }

    @PostMapping("/getScheduleByClass")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getScheduleByClass(@Valid @RequestBody DataRequest dataRequest) {
        return scheduleService.getScheduleByClass(dataRequest);
    }

    @PostMapping("/getScheduleByCourse")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getScheduleByCourse(@Valid @RequestBody DataRequest dataRequest) {
        return scheduleService.getScheduleByCourse(dataRequest);
    }

    @PostMapping("/getScheduleOptionList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getScheduleOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return scheduleService.getScheduleOptionList(dataRequest);
    }
    @GetMapping("/courseList")
    public DataResponse getCourseList() {
        System.out.println("=== Controller: getCourseList 被调用了 ===");
        return scheduleService.getCourseList();
    }

    @GetMapping("/teacherList")
    public DataResponse getTeacherList() {
        System.out.println("=== Controller: getTeacherList 被调用了 ===");
        return scheduleService.getTeacherList();
    }
}
