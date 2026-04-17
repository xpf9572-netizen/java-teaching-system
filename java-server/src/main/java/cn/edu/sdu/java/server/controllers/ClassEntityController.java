package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.ClassEntity;
import cn.edu.sdu.java.server.repositorys.ClassEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/classes")
public class ClassEntityController {
    @Autowired
    private ClassEntityRepository classEntityRepository;

    @GetMapping
    public Map<String, Object> getClasses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String classNum,
            @RequestParam(required = false) String className) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "classId"));
        Page<ClassEntity> classPage = classEntityRepository.findAll(pageable);

        List<Map<String, Object>> content = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ClassEntity c : classPage.getContent()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getClassId());
            m.put("classNum", c.getClassNum());
            m.put("className", c.getClassName());
            m.put("department", c.getDepartment());
            m.put("major", c.getMajor());
            m.put("counselor", c.getCounselor());
            m.put("phone", c.getPhone());
            m.put("studentCount", c.getStudentCount());
            m.put("grade", c.getGrade());
            m.put("status", c.getStatus());
            m.put("createTime", c.getCreateTime() != null ? sdf.format(c.getCreateTime()) : "");
            content.add(m);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("totalElements", classPage.getTotalElements());
        result.put("totalPages", classPage.getTotalPages());
        result.put("content", content);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getClass(@PathVariable Integer id) {
        Optional<ClassEntity> op = classEntityRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (op.isPresent()) {
            ClassEntity c = op.get();
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getClassId());
            m.put("classNum", c.getClassNum());
            m.put("className", c.getClassName());
            m.put("department", c.getDepartment());
            m.put("major", c.getMajor());
            m.put("counselor", c.getCounselor());
            m.put("phone", c.getPhone());
            m.put("studentCount", c.getStudentCount());
            m.put("grade", c.getGrade());
            m.put("status", c.getStatus());
            m.put("createTime", c.getCreateTime() != null ? sdf.format(c.getCreateTime()) : "");
            result.put("success", true);
            result.put("data", m);
        } else {
            result.put("success", false);
            result.put("msg", "班级不存在");
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> createClass(@RequestBody Map<String, Object> data) {
        ClassEntity c = new ClassEntity();
        updateClassFromData(c, data);
        c.setCreateTime(new Date());
        classEntityRepository.save(c);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", c);
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateClass(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        Optional<ClassEntity> op = classEntityRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            ClassEntity c = op.get();
            updateClassFromData(c, data);
            classEntityRepository.save(c);
            result.put("success", true);
            result.put("data", c);
        } else {
            result.put("success", false);
            result.put("msg", "班级不存在");
        }
        return result;
    }

    private void updateClassFromData(ClassEntity c, Map<String, Object> data) {
        if (data.get("classNum") != null) c.setClassNum((String) data.get("classNum"));
        if (data.get("className") != null) c.setClassName((String) data.get("className"));
        if (data.get("department") != null) c.setDepartment((String) data.get("department"));
        if (data.get("major") != null) c.setMajor((String) data.get("major"));
        if (data.get("counselor") != null) c.setCounselor((String) data.get("counselor"));
        if (data.get("phone") != null) c.setPhone((String) data.get("phone"));
        if (data.get("studentCount") != null) c.setStudentCount(((Number) data.get("studentCount")).intValue());
        if (data.get("grade") != null) c.setGrade((String) data.get("grade"));
        if (data.get("status") != null) c.setStatus((String) data.get("status"));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteClass(@PathVariable Integer id) {
        Optional<ClassEntity> op = classEntityRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            classEntityRepository.delete(op.get());
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("msg", "班级不存在");
        }
        return result;
    }
}
