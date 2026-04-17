package com.teach.studentadmin.controller;

import com.teach.studentadmin.dto.ApiResponse;
import com.teach.studentadmin.dto.PageResponse;
import com.teach.studentadmin.entity.Course;
import com.teach.studentadmin.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<PageResponse<Course>> findAll(
            @RequestParam(required = false) Map<String, Object> params,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (params == null) {
                params = Map.of();
            }
            Page<Course> pageResult = courseService.findAll(params, page - 1, size);
            PageResponse<Course> response = new PageResponse<>(
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

    @GetMapping("/all")
    public ApiResponse<List<Course>> findAll() {
        try {
            return ApiResponse.success(courseService.findAll());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Course> findById(@PathVariable Long id) {
        try {
            return courseService.findById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("课程不存在"));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Course> save(@RequestBody Course course) {
        try {
            return ApiResponse.success(courseService.save(course));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Course> update(@PathVariable Long id, @RequestBody Course course) {
        try {
            course.setId(id);
            return ApiResponse.success(courseService.save(course));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        try {
            courseService.delete(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success(courseService.count());
    }
}
