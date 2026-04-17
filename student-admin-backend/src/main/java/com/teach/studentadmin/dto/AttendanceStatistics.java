package com.teach.studentadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatistics {
    private Long courseId;
    private String courseName;
    private Long totalRecords;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Long leaveCount;
    private Double attendanceRate;
    private Map<String, Long> statusDistribution;
}
