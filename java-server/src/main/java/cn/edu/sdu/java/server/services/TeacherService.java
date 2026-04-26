package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Optional;

@Service
public class TeacherService {
    private static final Logger log = LoggerFactory.getLogger(TeacherService.class);
    private final PersonRepository personRepository;
    private final TeacherRepository teacherRepository;

    public TeacherService(PersonRepository personRepository, TeacherRepository teacherRepository) {
        this.personRepository = personRepository;
        this.teacherRepository = teacherRepository;
    }

    public Map<String, Object> getMapFromTeacher(Teacher t) {
        Map<String, Object> m = new HashMap<>();
        Person p;
        if (t == null)
            return m;
        m.put("title", t.getTitle());
        m.put("degree", t.getDegree());
        p = t.getPerson();
        if (p == null)
            return m;
        m.put("personId", t.getPersonId());
        m.put("teacherNum", p.getNum());
        m.put("name", p.getName());
        m.put("dept", p.getDept());
        m.put("card", p.getCard());
        String gender = p.getGender();
        m.put("gender", gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender));
        m.put("birthday", p.getBirthday());
        m.put("email", p.getEmail());
        m.put("phone", p.getPhone());
        m.put("address", p.getAddress());
        m.put("introduce", p.getIntroduce());
        return m;
    }

    public List<Map<String, Object>> getTeacherMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Teacher> tList = teacherRepository.findTeacherListByNumName(numName);
        if (tList == null || tList.isEmpty())
            return dataList;
        for (Teacher teacher : tList) {
            dataList.add(getMapFromTeacher(teacher));
        }
        return dataList;
    }

    public DataResponse getTeacherList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Teacher t = null;
        Optional<Teacher> op;
        if (personId != null) {
            op = teacherRepository.findById(personId);
            if (op.isPresent()) {
                t = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTeacher(t));
    }

    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Teacher t = null;
        Optional<Teacher> op;
        if (personId != null && personId > 0) {
            op = teacherRepository.findById(personId);
            if (op.isPresent()) {
                t = op.get();
                Person p = t.getPerson();
                CommonMethod.logDeleteOperation("teacher", personId);
                teacherRepository.delete(t);
                personRepository.delete(p);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse teacherEditSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map<String, Object> form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form, "teacherNum");
        Teacher t = null;
        Person p;
        Optional<Teacher> op;
        boolean isNew = false;
        if (personId != null) {
            op = teacherRepository.findById(personId);
            if (op.isPresent()) {
                t = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num);
        if (nOp.isPresent()) {
            if (t == null || !t.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新工号已经存在，不能添加或修改！");
            }
        }
        if (t == null) {
            p = new Person();
            p.setNum(num);
            p.setType("2");
            personRepository.saveAndFlush(p);
            personId = p.getPersonId();
            t = new Teacher();
            t.setPersonId(personId);
            teacherRepository.saveAndFlush(t);
            isNew = true;
        } else {
            p = t.getPerson();
        }
        personId = p.getPersonId();
        if (!num.equals(p.getNum())) {
            p.setNum(num);
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        p.setIntroduce(CommonMethod.getString(form, "introduce"));
        personRepository.save(p);
        t.setTitle(CommonMethod.getString(form, "title"));
        t.setDegree(CommonMethod.getString(form, "degree"));
        teacherRepository.save(t);
        return CommonMethod.getReturnData(t.getPersonId());
    }

    public OptionItemList getTeacherOptionList(DataRequest dataRequest) {
        List<Teacher> tList = teacherRepository.findAll();
        List<OptionItem> itemList = new ArrayList<>();
        for (Teacher t : tList) {
            itemList.add(new OptionItem(t.getPersonId(), t.getPersonId() + "", t.getPerson().getNum() + "-" + t.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
}
