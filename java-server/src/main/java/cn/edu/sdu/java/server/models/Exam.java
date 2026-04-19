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
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer examId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @NotBlank
    @Column(name = "semester")
    private String semester;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "exam_date")
    private Date examDate;

    @NotBlank
    @Column(name = "exam_time")
    private String examTime;

    @NotBlank
    @Column(name = "exam_location")
    private String examLocation;

    @ManyToOne
    @JoinColumn(name = "invigilator_id")
    private Teacher invigilator;

    @Column(name = "exam_type")
    private String examType;

    private Integer totalStudents;

    private String remark;

    @Version
    private Integer version;
}
