package com.teach.studentadmin.service;

import com.teach.studentadmin.dto.AttendanceStatistics;
import com.teach.studentadmin.dto.CourseStatistics;
import com.teach.studentadmin.entity.Course;
import com.teach.studentadmin.entity.Enrollment;
import com.teach.studentadmin.repository.AttendanceRepository;
import com.teach.studentadmin.repository.EnrollmentRepository;
import com.teach.studentadmin.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;

    public CourseStatistics getCourseStatistics(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        CourseStatistics stats = new CourseStatistics();
        stats.setCourseId(courseId);
        stats.setCourseName(course.getCourseName());

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        stats.setTotalStudents(enrollments.size());

        Double avgScore = enrollmentRepository.findAverageScoreByCourseId(courseId);
        Double maxScore = enrollmentRepository.findMaxScoreByCourseId(courseId);
        Double minScore = enrollmentRepository.findMinScoreByCourseId(courseId);

        stats.setAverageScore(avgScore != null ? avgScore : 0.0);
        stats.setMaxScore(maxScore != null ? maxScore : 0.0);
        stats.setMinScore(minScore != null ? minScore : 0.0);

        Map<String, Long> scoreDistribution = new HashMap<>();
        for (Enrollment e : enrollments) {
            if (e.getScore() != null) {
                String range = getScoreRange(e.getScore());
                scoreDistribution.put(range, scoreDistribution.getOrDefault(range, 0L) + 1);
            }
        }
        stats.setScoreDistribution(scoreDistribution);

        List<CourseStatistics.ScoreRecord> sortedEnrollments = enrollments.stream()
                .filter(e -> e.getScore() != null)
                .sorted(Comparator.comparing(Enrollment::getScore).reversed())
                .limit(5)
                .map(e -> new CourseStatistics.ScoreRecord(e.getStudentName(), e.getScore()))
                .collect(Collectors.toList());
        stats.setTopStudents(sortedEnrollments);

        List<CourseStatistics.ScoreRecord> bottomEnrollments = enrollments.stream()
                .filter(e -> e.getScore() != null)
                .sorted(Comparator.comparing(Enrollment::getScore))
                .limit(5)
                .map(e -> new CourseStatistics.ScoreRecord(e.getStudentName(), e.getScore()))
                .collect(Collectors.toList());
        stats.setBottomStudents(bottomEnrollments);

        return stats;
    }

    private String getScoreRange(Double score) {
        if (score >= 90) return "90-100";
        if (score >= 80) return "80-89";
        if (score >= 70) return "70-79";
        if (score >= 60) return "60-69";
        return "0-59";
    }

    public AttendanceStatistics getAttendanceStatistics(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        AttendanceStatistics stats = new AttendanceStatistics();
        stats.setCourseId(courseId);
        stats.setCourseName(course.getCourseName());

        List<Object[]> statusCounts = attendanceRepository.countByCourseIdGroupByStatus(courseId);
        Map<String, Long> statusDistribution = new HashMap<>();
        long total = 0;
        long presentCount = 0;
        long absentCount = 0;
        long lateCount = 0;
        long leaveCount = 0;

        for (Object[] row : statusCounts) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            statusDistribution.put(status, count);
            total += count;
            switch (status) {
                case "PRESENT" -> presentCount = count;
                case "ABSENT" -> absentCount = count;
                case "LATE" -> lateCount = count;
                case "LEAVE" -> leaveCount = count;
            }
        }

        stats.setTotalRecords(total);
        stats.setPresentCount(presentCount);
        stats.setAbsentCount(absentCount);
        stats.setLateCount(lateCount);
        stats.setLeaveCount(leaveCount);
        stats.setStatusDistribution(statusDistribution);

        double attendanceRate = total > 0 ? (double) (presentCount + lateCount) / total * 100 : 0;
        stats.setAttendanceRate(Math.round(attendanceRate * 100.0) / 100.0);

        return stats;
    }

    public Map<String, Object> getOverviewStatistics() {
        Map<String, Object> overview = new HashMap<>();

        long totalStudents = enrollmentRepository.findAll().stream()
                .map(Enrollment::getStudentId)
                .distinct()
                .count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long totalAttendances = attendanceRepository.count();

        overview.put("totalStudents", totalStudents);
        overview.put("totalCourses", totalCourses);
        overview.put("totalEnrollments", totalEnrollments);
        overview.put("totalAttendances", totalAttendances);

        List<Course> courses = courseRepository.findAll();
        double overallAvgScore = courses.stream()
                .mapToDouble(c -> {
                    Double avg = enrollmentRepository.findAverageScoreByCourseId(c.getId());
                    return avg != null ? avg : 0;
                })
                .average()
                .orElse(0);
        overview.put("overallAverageScore", Math.round(overallAvgScore * 100.0) / 100.0);

        return overview;
    }
}
