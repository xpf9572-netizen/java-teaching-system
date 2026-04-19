package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.SystemInfoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/system")
public class SystemInfoController {
    private final SystemInfoService systemInfoService;

    public SystemInfoController(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    @GetMapping("/info")
    public DataResponse getSystemInfo() {
        return systemInfoService.getSystemInfo(new DataRequest());
    }

    @PostMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse saveSystemInfo(@Valid @RequestBody DataRequest dataRequest) {
        return systemInfoService.saveSystemInfo(dataRequest);
    }

    @PostMapping("/info/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteSystemInfo(@Valid @RequestBody DataRequest dataRequest) {
        return systemInfoService.deleteSystemInfo(dataRequest);
    }
}
