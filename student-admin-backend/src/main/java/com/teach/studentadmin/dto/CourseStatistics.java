package com.teach.studentadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatistics {
    private Long courseId;
    private String courseName;
    private Double averageScore;
    private Double maxScore;
    private Double minScore;
    private Integer totalStudents;
    private Map<String, Long> scoreDistribution;
    private List<ScoreRecord> topStudents;
    private List<ScoreRecord> bottomStudents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreRecord {
        private String studentName;
        private Double score;
    }
}
