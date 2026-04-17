package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final PersonRepository personRepository;

    public TeacherService(TeacherRepository teacherRepository, PersonRepository personRepository) {
        this.teacherRepository = teacherRepository;
        this.personRepository = personRepository;
    }

    public DataResponse getTeacherList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName == null) numName = "";
        List<Teacher> teachers = teacherRepository.findTeacherListByNumName(numName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Teacher t : teachers) {
            m = new HashMap<>();
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
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse teacherSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        String teacherNum = dataRequest.getString("teacherNum");
        String name = dataRequest.getString("name");
        String gender = dataRequest.getString("gender");
        String department = dataRequest.getString("department");
        String phone = dataRequest.getString("phone");
        String email = dataRequest.getString("email");
        String address = dataRequest.getString("address");
        String introduce = dataRequest.getString("introduce");
        String title = dataRequest.getString("title");
        String degree = dataRequest.getString("degree");

        Optional<Teacher> op;
        Teacher teacher = null;
        Person person = null;

        if (personId != null) {
            op = teacherRepository.findById(personId);
            if (op.isPresent()) {
                teacher = op.get();
                person = teacher.getPerson();
            }
        }

        if (teacher == null) {
            teacher = new Teacher();
            person = new Person();
            person.setType("2"); // 教师类型
        }

        person.setNum(teacherNum);
        person.setName(name);
        person.setGender(gender);
        person.setDept(department);
        person.setPhone(phone);
        person.setEmail(email);
        person.setAddress(address);
        person.setIntroduce(introduce);
        person = personRepository.save(person);

        teacher.setPerson(person);
        teacher.setTitle(title);
        teacher.setDegree(degree);
        teacherRepository.save(teacher);

        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if (personId != null) {
            Optional<Teacher> op = teacherRepository.findById(personId);
            if (op.isPresent()) {
                Teacher teacher = op.get();
                teacherRepository.delete(teacher);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
