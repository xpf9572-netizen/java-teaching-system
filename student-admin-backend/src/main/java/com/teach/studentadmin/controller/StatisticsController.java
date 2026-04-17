package com.teach.studentadmin.controller;

import com.teach.studentadmin.dto.ApiResponse;
import com.teach.studentadmin.dto.AttendanceStatistics;
import com.teach.studentadmin.dto.CourseStatistics;
import com.teach.studentadmin.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/course/{courseId}")
    public ApiResponse<CourseStatistics> getCourseStatistics(@PathVariable Long courseId) {
        try {
            return ApiResponse.success(statisticsService.getCourseStatistics(courseId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/attendance/{courseId}")
    public ApiResponse<AttendanceStatistics> getAttendanceStatistics(@PathVariable Long courseId) {
        try {
            return ApiResponse.success(statisticsService.getAttendanceStatistics(courseId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverviewStatistics() {
        try {
            return ApiResponse.success(statisticsService.getOverviewStatistics());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
