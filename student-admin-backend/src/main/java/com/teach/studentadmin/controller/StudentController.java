package com.teach.studentadmin.controller;

import com.teach.studentadmin.dto.ApiResponse;
import com.teach.studentadmin.dto.PageResponse;
import com.teach.studentadmin.entity.Student;
import com.teach.studentadmin.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ApiResponse<PageResponse<Student>> findAll(
            @RequestParam(required = false) Map<String, Object> params,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (params == null) {
                params = Map.of();
            }
            Page<Student> pageResult = studentService.findAll(params, page - 1, size);
            PageResponse<Student> response = new PageResponse<>(
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
    public ApiResponse<List<Student>> findAll() {
        try {
            return ApiResponse.success(studentService.findAll());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Student> findById(@PathVariable Long id) {
        try {
            return studentService.findById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("学生不存在"));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<Student> save(@RequestBody Student student) {
        try {
            return ApiResponse.success(studentService.save(student));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Student> update(@PathVariable Long id, @RequestBody Student student) {
        try {
            student.setId(id);
            return ApiResponse.success(studentService.save(student));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        try {
            studentService.delete(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success(studentService.count());
    }
}
