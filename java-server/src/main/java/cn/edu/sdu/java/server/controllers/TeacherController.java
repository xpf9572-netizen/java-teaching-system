package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.services.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private PersonRepository personRepository;

    /**
     * 获取教师信息 - 返回DataResponse格式
     * 前端调用: POST /api/teachers/getTeacherInfo
     */
    @PostMapping("/getTeacherInfo")
    public DataResponse getTeacherInfo(@RequestBody DataRequest request) {
        Integer personId = request.getInteger("personId");
        if (personId == null) {
            return CommonMethod.getReturnMessageError("教师ID不能为空");
        }

        Optional<Teacher> op = teacherRepository.findById(personId);
        if (op.isPresent()) {
            Teacher t = op.get();
            Person p = t.getPerson();
            Map<String, Object> m = new HashMap<>();
            if (p != null) {
                m.put("id", t.getPersonId());
                m.put("teacherNum", p.getNum());
                m.put("name", p.getName());
                m.put("gender", p.getGender());
                m.put("department", p.getDept());
                m.put("phone", p.getPhone());
                m.put("email", p.getEmail());
                m.put("address", p.getAddress());
                m.put("introduce", p.getIntroduce());
            }
            m.put("title", t.getTitle());
            m.put("degree", t.getDegree());
            return CommonMethod.getReturnData(m);
        } else {
            return CommonMethod.getReturnMessageError("教师不存在");
        }
    }

    @GetMapping
    public Map<String, Object> getTeachers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String teacherNum,
            @RequestParam(required = false) String name) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "personId"));
        Page<Teacher> teacherPage = teacherRepository.findAll(pageable);

        List<Map<String, Object>> content = new ArrayList<>();
        for (Teacher t : teacherPage.getContent()) {
            Map<String, Object> m = new HashMap<>();
            Person p = t.getPerson();
            if (p != null) {
                m.put("id", t.getPersonId());
                m.put("teacherNum", p.getNum());
                m.put("name", p.getName());
                m.put("gender", p.getGender());
                m.put("department", p.getDept());
                m.put("phone", p.getPhone());
                m.put("email", p.getEmail());
                m.put("address", p.getAddress());
                m.put("introduce", p.getIntroduce());
            }
            m.put("title", t.getTitle());
            m.put("degree", t.getDegree());
            content.add(m);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("totalElements", teacherPage.getTotalElements());
        result.put("totalPages", teacherPage.getTotalPages());
        result.put("content", content);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getTeacher(@PathVariable Integer id) {
        Optional<Teacher> op = teacherRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            Teacher t = op.get();
            Person p = t.getPerson();
            Map<String, Object> m = new HashMap<>();
            if (p != null) {
                m.put("id", t.getPersonId());
                m.put("teacherNum", p.getNum());
                m.put("name", p.getName());
                m.put("gender", p.getGender());
                m.put("department", p.getDept());
                m.put("phone", p.getPhone());
                m.put("email", p.getEmail());
                m.put("address", p.getAddress());
                m.put("introduce", p.getIntroduce());
            }
            m.put("title", t.getTitle());
            m.put("degree", t.getDegree());
            result.put("success", true);
            result.put("data", m);
        } else {
            result.put("success", false);
            result.put("msg", "教师不存在");
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> createTeacher(@RequestBody Map<String, Object> data) {
        Teacher teacher = new Teacher();
        Person person = new Person();
        person.setType("2");

        person.setNum((String) data.get("teacherNum"));
        person.setName((String) data.get("name"));
        person.setGender((String) data.get("gender"));
        person.setDept((String) data.get("department"));
        person.setPhone((String) data.get("phone"));
        person.setEmail((String) data.get("email"));
        person.setAddress((String) data.get("address"));
        person.setIntroduce((String) data.get("introduce"));
        person = personRepository.save(person);

        teacher.setPerson(person);
        teacher.setTitle((String) data.get("title"));
        teacher.setDegree((String) data.get("degree"));
        teacherRepository.save(teacher);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", teacher);
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateTeacher(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        Optional<Teacher> op = teacherRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            Teacher teacher = op.get();
            Person person = teacher.getPerson();
            if (person == null) {
                person = new Person();
                person.setType("2");
            }

            if (data.get("teacherNum") != null) person.setNum((String) data.get("teacherNum"));
            if (data.get("name") != null) person.setName((String) data.get("name"));
            if (data.get("gender") != null) person.setGender((String) data.get("gender"));
            if (data.get("department") != null) person.setDept((String) data.get("department"));
            if (data.get("phone") != null) person.setPhone((String) data.get("phone"));
            if (data.get("email") != null) person.setEmail((String) data.get("email"));
            if (data.get("address") != null) person.setAddress((String) data.get("address"));
            if (data.get("introduce") != null) person.setIntroduce((String) data.get("introduce"));
            person = personRepository.save(person);

            teacher.setPerson(person);
            if (data.get("title") != null) teacher.setTitle((String) data.get("title"));
            if (data.get("degree") != null) teacher.setDegree((String) data.get("degree"));
            teacherRepository.save(teacher);

            result.put("success", true);
            result.put("data", teacher);
        } else {
            result.put("success", false);
            result.put("msg", "教师不存在");
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTeacher(@PathVariable Integer id) {
        Optional<Teacher> op = teacherRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (op.isPresent()) {
            teacherRepository.delete(op.get());
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("msg", "教师不存在");
        }
        return result;
    }
}
