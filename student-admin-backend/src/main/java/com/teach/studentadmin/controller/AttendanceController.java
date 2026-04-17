package com.teach.studentadmin.controller;

import com.teach.studentadmin.dto.ApiResponse;
import com.teach.studentadmin.dto.PageResponse;
import com.teach.studentadmin.entity.Attendance;
import com.teach.studentadmin.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ApiResponse<PageResponse<Attendance>> findAll(
            @RequestParam(required = false) Map<String, Object> params,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (params == null) {
                params = Map.of();
            }
            Page<Attendance> pageResult = attendanceService.findAll(params, page - 1, size);
            PageResponse<Attendance> response = new PageResponse<>(
                    pageResult.getContent(),
                    pageResult.getTotalElements(),
                    pageResult.getTotalPages(),
                    pageResult.getNumber() + 1,
                    pageResult.getSize(),
                    pageResult.isFirst(),
                    pageResult.isLast()
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Attendance>> findByStudentId(@PathVariable Long studentId) {
        try {
            return ApiResponse.success(attendanceService.findByStudentId(studentId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Attendance>> findByCourseId(@PathVariable Long courseId) {
        try {
            return ApiResponse.success(attendanceService.findByCourseId(courseId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Attendance> findById(@PathVariable Long id) {
        try {
            return attendanceService.findById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("考勤记录不存在"));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Attendance> save(@RequestBody Attendance attendance) {
        try {
            return ApiResponse.success(attendanceService.save(attendance));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Attendance> update(@PathVariable Long id, @RequestBody Attendance attendance) {
        try {
            attendance.setId(id);
            return ApiResponse.success(attendanceService.save(attendance));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        try {
            attendanceService.delete(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success(attendanceService.count());
    }
}
