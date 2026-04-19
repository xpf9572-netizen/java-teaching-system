package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ImportExportService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/importExport")
public class ImportExportController {
    private final ImportExportService importExportService;

    public ImportExportController(ImportExportService importExportService) {
        this.importExportService = importExportService;
    }

    @GetMapping("/exportStudentRoster")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> exportStudentRoster() {
        return importExportService.exportStudentRoster();
    }

    @PostMapping("/exportScoreRecords")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> exportScoreRecords(@Valid @RequestBody DataRequest dataRequest) {
        return importExportService.exportScoreRecords(dataRequest);
    }

    @GetMapping("/exportExamArrangements")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> exportExamArrangements() {
        return importExportService.exportExamArrangements(new DataRequest());
    }

    @PostMapping("/importStudents")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse importStudents(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return CommonMethod.getReturnMessageError("请选择要导入的文件");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return CommonMethod.getReturnMessageError("请上传Excel文件(.xlsx或.xls)");
        }
        try {
            return importExportService.importStudents(file.getInputStream(), fileName);
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/importScores")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse importScores(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return CommonMethod.getReturnMessageError("请选择要导入的文件");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return CommonMethod.getReturnMessageError("请上传Excel文件(.xlsx或.xls)");
        }
        try {
            return importExportService.importScores(file.getInputStream(), fileName);
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("文件上传失败: " + e.getMessage());
        }
    }
}
