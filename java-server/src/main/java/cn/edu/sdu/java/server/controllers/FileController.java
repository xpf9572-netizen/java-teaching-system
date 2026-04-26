package cn.edu.sdu.java.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 1);
            error.put("msg", "文件不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            File dest = new File(UPLOAD_DIR + newFilename);
            file.transferTo(dest);

            // 返回文件URL
            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("msg", "上传成功");
            result.put("url", "/uploads/" + newFilename);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 1);
            error.put("msg", "上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
