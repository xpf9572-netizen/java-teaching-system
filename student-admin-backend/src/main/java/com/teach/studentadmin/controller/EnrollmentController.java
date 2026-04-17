package com.teach.studentadmin.controller;

import com.teach.studentadmin.dto.ApiResponse;
import com.teach.studentadmin.dto.PageResponse;
import com.teach.studentadmin.entity.Enrollment;
import com.teach.studentadmin.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    public ApiResponse<PageResponse<Enrollment>> findAll(
            @RequestParam(required = false) Map<String, Object> params,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (params == null) {
                params = Map.of();
            }
            Page<Enrollment> pageResult = enrollmentService.findAll(params, page - 1, size);
            PageResponse<Enrollment> response = new PageResponse<>(
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
    public ApiResponse<List<Enrollment>> findByStudentId(@PathVariable Long studentId) {
        try {
            return ApiResponse.success(enrollmentService.findByStudentId(studentId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Enrollment>> findByCourseId(@PathVariable Long courseId) {
        try {
            return ApiResponse.success(enrollmentService.findByCourseId(courseId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Enrollment> findById(@PathVariable Long id) {
        try {
            return enrollmentService.findById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("选课记录不存在"));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Enrollment> save(@RequestBody Enrollment enrollment) {
        try {
            return ApiResponse.success(enrollmentService.save(enrollment));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Enrollment> update(@PathVariable Long id, @RequestBody Enrollment enrollment) {
        try {
            enrollment.setId(id);
            return ApiResponse.success(enrollmentService.save(enrollment));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        try {
            enrollmentService.delete(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success(enrollmentService.count());
    }
}
