package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ExamService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/exam")
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAllExams() {
        return examService.getExamList(new DataRequest());
    }

    @PostMapping("/getExamList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getExamList(@Valid @RequestBody DataRequest dataRequest) {
        return examService.getExamList(dataRequest);
    }

    @PostMapping("/examSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse examSave(@Valid @RequestBody DataRequest dataRequest) {
        return examService.examSave(dataRequest);
    }

    @PostMapping("/examDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse examDelete(@Valid @RequestBody DataRequest dataRequest) {
        return examService.examDelete(dataRequest);
    }

    @PostMapping("/getViolationList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getViolationList(@Valid @RequestBody DataRequest dataRequest) {
        return examService.getViolationList(dataRequest);
    }

    @PostMapping("/violationSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse violationSave(@Valid @RequestBody DataRequest dataRequest) {
        return examService.violationSave(dataRequest);
    }

    @PostMapping("/violationDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse violationDelete(@Valid @RequestBody DataRequest dataRequest) {
        return examService.violationDelete(dataRequest);
    }
}
