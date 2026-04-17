package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "class_entity")
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer classId;

    @Size(max = 20)
    private String classNum;

    @Size(max = 50)
    private String className;

    @Size(max = 50)
    private String department;

    @Size(max = 50)
    private String major;

    @Size(max = 20)
    private String counselor;

    @Size(max = 20)
    private String phone;

    private Integer studentCount;

    @Size(max = 10)
    private String grade;

    @Size(max = 20)
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
