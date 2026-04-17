package com.teach.studentadmin.controller;

import com.teach.studentadmin.dto.ApiResponse;
import com.teach.studentadmin.dto.PageRequest;
import com.teach.studentadmin.dto.PageResponse;
import com.teach.studentadmin.entity.ClassEntity;
import com.teach.studentadmin.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping
    public ApiResponse<PageResponse<ClassEntity>> findAll(
            @RequestParam(required = false) Map<String, Object> params,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (params == null) {
                params = Map.of();
            }
            Page<ClassEntity> pageResult = classService.findAll(params, page - 1, size);
            PageResponse<ClassEntity> response = new PageResponse<>(
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
    public ApiResponse<List<ClassEntity>> findAll() {
        try {
            return ApiResponse.success(classService.findAll());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassEntity> findById(@PathVariable Long id) {
        try {
            return classService.findById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("班级不存在"));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<ClassEntity> save(@RequestBody ClassEntity classEntity) {
        try {
            return ApiResponse.success(classService.save(classEntity));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassEntity> update(@PathVariable Long id, @RequestBody ClassEntity classEntity) {
        try {
            classEntity.setId(id);
            return ApiResponse.success(classService.save(classEntity));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        try {
            classService.delete(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success(classService.count());
    }
}
