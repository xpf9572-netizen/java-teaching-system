package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.SystemInfo;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.SystemInfoRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SystemInfoService {
    private final SystemInfoRepository systemInfoRepository;
    private final SystemService systemService;

    public SystemInfoService(SystemInfoRepository systemInfoRepository, SystemService systemService) {
        this.systemInfoRepository = systemInfoRepository;
        this.systemService = systemService;
    }

    public List<Map<String, Object>> getSystemInfoList() {
        List<SystemInfo> sList = systemInfoRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        if (sList == null || sList.isEmpty())
            return dataList;
        for (SystemInfo s : sList) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.getId());
            m.put("name", s.getName());
            m.put("value", s.getValue());
            m.put("des", s.getDes());
            dataList.add(m);
        }
        return dataList;
    }

    public DataResponse getSystemInfo(DataRequest dataRequest) {
        List<Map<String, Object>> dataList = getSystemInfoList();
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse saveSystemInfo(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer id = CommonMethod.getInteger(form, "id");
        String name = CommonMethod.getString(form, "name");
        String value = CommonMethod.getString(form, "value");
        String des = CommonMethod.getString(form, "des");

        SystemInfo systemInfo = null;
        if (id != null && id > 0) {
            Optional<SystemInfo> op = systemInfoRepository.findById(id);
            if (op.isPresent()) {
                systemInfo = op.get();
            }
        }
        if (systemInfo == null) {
            systemInfo = new SystemInfo();
        }
        systemInfo.setName(name);
        systemInfo.setValue(value);
        systemInfo.setDes(des);
        systemInfoRepository.save(systemInfo);
        systemService.initSystem();
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse deleteSystemInfo(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        if (id != null && id > 0) {
            Optional<SystemInfo> op = systemInfoRepository.findById(id);
            op.ifPresent(systemInfoRepository::delete);
        }
        return CommonMethod.getReturnMessageOK();
    }
}
