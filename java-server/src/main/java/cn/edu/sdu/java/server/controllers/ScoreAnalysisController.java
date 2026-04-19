package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ScoreAnalysisService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/scoreAnalysis")
public class ScoreAnalysisController {
    private final ScoreAnalysisService analysisService;

    public ScoreAnalysisController(ScoreAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/getCourseAnalysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getCourseAnalysis(@Valid @RequestBody DataRequest dataRequest) {
        return analysisService.getCourseAnalysis(dataRequest);
    }

    @PostMapping("/getStudentAnalysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse getStudentAnalysis(@Valid @RequestBody DataRequest dataRequest) {
        return analysisService.getStudentAnalysis(dataRequest);
    }

    @PostMapping("/getClassAnalysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getClassAnalysis(@Valid @RequestBody DataRequest dataRequest) {
        return analysisService.getClassAnalysis(dataRequest);
    }

    @PostMapping("/getWarningStudents")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getWarningStudents(@Valid @RequestBody DataRequest dataRequest) {
        return analysisService.getWarningStudents(dataRequest);
    }

    @PostMapping("/getOverallStatistics")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getOverallStatistics(@Valid @RequestBody DataRequest dataRequest) {
        return analysisService.getOverallStatistics(dataRequest);
    }
}
