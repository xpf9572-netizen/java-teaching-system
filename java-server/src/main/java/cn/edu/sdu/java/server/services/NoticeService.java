package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Notice;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.NoticeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public DataResponse getNoticeList(DataRequest dataRequest) {
        String audience = dataRequest.getString("audience");
        if (audience == null)
            audience = "";
        List<Notice> nList = noticeRepository.findNoticeListByAudience(audience);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Notice n : nList) {
            m = new HashMap<>();
            m.put("noticeId", n.getNoticeId() != null ? n.getNoticeId().toString() : "");
            m.put("title", n.getTitle());
            m.put("content", n.getContent());
            m.put("publisher", n.getPublisher());
            m.put("publishTime", n.getPublishTime() != null ? n.getPublishTime().toString() : "");
            m.put("type", n.getType());
            m.put("targetAudience", n.getTargetAudience());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getNoticeById(DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");
        if (noticeId == null) {
            return CommonMethod.getReturnMessageError("noticeId is required");
        }
        Optional<Notice> op = noticeRepository.findById(noticeId);
        if (op.isEmpty()) {
            return CommonMethod.getReturnMessageError("Notice not found");
        }
        Notice n = op.get();
        Map<String, Object> m = new HashMap<>();
        m.put("noticeId", n.getNoticeId() != null ? n.getNoticeId().toString() : "");
        m.put("title", n.getTitle());
        m.put("content", n.getContent());
        m.put("publisher", n.getPublisher());
        m.put("publishTime", n.getPublishTime() != null ? n.getPublishTime().toString() : "");
        m.put("type", n.getType());
        m.put("targetAudience", n.getTargetAudience());
        return CommonMethod.getReturnData(m);
    }

    @Transactional
    public DataResponse noticeSave(DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");
        String title = dataRequest.getString("title");
        String content = dataRequest.getString("content");
        String publisher = dataRequest.getString("publisher");
        Date publishTime = dataRequest.getTime("publishTime");
        String type = dataRequest.getString("type");
        String targetAudience = dataRequest.getString("targetAudience");

        Optional<Notice> op;
        Notice n = null;

        if (noticeId != null) {
            op = noticeRepository.findById(noticeId);
            if (op.isPresent())
                n = op.get();
        }
        if (n == null)
            n = new Notice();

        n.setTitle(title);
        n.setContent(content);
        n.setPublisher(publisher);
        n.setPublishTime(publishTime != null ? publishTime : new Date());
        n.setType(type);
        n.setTargetAudience(targetAudience);

        noticeRepository.save(n);
        return CommonMethod.getReturnMessageOK();
    }

    @Transactional
    public DataResponse noticeDelete(DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");
        Optional<Notice> op;
        Notice n = null;
        if (noticeId != null) {
            op = noticeRepository.findById(noticeId);
            if (op.isPresent()) {
                n = op.get();
                CommonMethod.logDeleteOperation("notice", noticeId);
                noticeRepository.delete(n);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
