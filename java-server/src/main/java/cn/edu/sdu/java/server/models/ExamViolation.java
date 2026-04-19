package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "exam_violation")
public class ExamViolation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer violationId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @NotBlank
    @Column(name = "violation_type")
    private String violationType;

    @Column(name = "violation_desc")
    private String violationDesc;

    @Column(name = "punishment")
    private String punishment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "record_time")
    private Date recordTime;

    @Column(name = "record_operator")
    private Integer recordOperator;

    private String remark;
}
