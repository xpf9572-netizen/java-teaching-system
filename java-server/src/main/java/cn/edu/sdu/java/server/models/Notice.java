package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*
 * Notice 通知公告实体类
 * Integer noticeId 通知公告表主键 notice_id
 * String title 标题
 * String content 内容
 * String publisher 发布人
 * Date publishTime 发布时间
 * String type 类型
 * String targetAudience 目标受众
 */

@Getter
@Setter
@Entity
@Table(name = "notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticeId;

    @Version
    private int version;

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 4000)
    private String content;

    @Size(max = 50)
    private String publisher;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publishTime;

    @Size(max = 20)
    private String type;

    @Size(max = 50)
    private String targetAudience;
}
