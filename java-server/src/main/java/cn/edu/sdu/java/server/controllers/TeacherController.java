package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    /**
     * 获取教师信息
     * 前端调用: POST /api/teachers/getTeacherInfo
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/getTeacherInfo")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherInfo(dataRequest);
    }

    /**
     * 获取教师列表
     * 前端调用: POST /api/teachers/getTeacherList
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/getTeacherList")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherList(dataRequest);
    }

    /**
     * 教师编辑保存 - 新增或更新
     * 前端调用: POST /api/teachers/teacherEditSave
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/teacherEditSave")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherEditSave(dataRequest);
    }

    /**
     * 删除教师
     * 前端调用: POST /api/teachers/teacherDelete
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/teacherDelete")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherDelete(dataRequest);
    }
}
