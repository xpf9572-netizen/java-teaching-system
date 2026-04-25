package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.NoticeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notice")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/list")
    public DataResponse getNoticeList(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.getNoticeList(dataRequest);
    }

    @PostMapping("/getById")
    public DataResponse getNoticeById(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.getNoticeById(dataRequest);
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse noticeSave(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.noticeSave(dataRequest);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse noticeDelete(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.noticeDelete(dataRequest);
    }
}
