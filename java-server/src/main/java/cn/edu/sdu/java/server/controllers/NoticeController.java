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

    @GetMapping("/list")
    public DataResponse getNoticeList(@RequestParam(required = false) String audience) {
        DataRequest dataRequest = new DataRequest();
        if (audience != null) {
            dataRequest.add("audience", audience);
        }
        return noticeService.getNoticeList(dataRequest);
    }

    @GetMapping("/{id}")
    public DataResponse getNoticeById(@PathVariable Integer id) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("noticeId", id);
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
