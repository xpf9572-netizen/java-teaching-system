package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScoreAnalysisService {
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;

    public ScoreAnalysisService(ScoreRepository scoreRepository, StudentRepository studentRepository) {
        this.scoreRepository = scoreRepository;
        this.studentRepository = studentRepository;
    }

    public DataResponse getCourseAnalysis(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null) {
            return CommonMethod.getReturnMessageError("courseId不能为空");
        }

        List<Score> scores = scoreRepository.findByStudentCourse(0, courseId);
        if (scores.isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("courseId", courseId);
            emptyResult.put("totalStudents", 0);
            emptyResult.put("avgScore", 0);
            emptyResult.put("maxScore", 0);
            emptyResult.put("minScore", 0);
            emptyResult.put("passCount", 0);
            emptyResult.put("passRate", 0);
            emptyResult.put("distribution", new HashMap<String, Integer>());
            return CommonMethod.getReturnData(emptyResult);
        }

        return CommonMethod.getReturnData(analyzeScores(scores, courseId));
    }

    public DataResponse getStudentAnalysis(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if (studentId == null) {
            studentId = CommonMethod.getPersonId();
        }

        List<Score> scores = scoreRepository.findByStudentPersonId(studentId);
        if (scores.isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("studentId", studentId);
            emptyResult.put("totalCourses", 0);
            emptyResult.put("avgScore", 0);
            emptyResult.put("totalCredits", 0);
            emptyResult.put("passedCourses", 0);
            emptyResult.put("failedCourses", 0);
            return CommonMethod.getReturnData(emptyResult);
        }

        return CommonMethod.getReturnData(analyzeStudentScores(scores, studentId));
    }

    public DataResponse getClassAnalysis(DataRequest dataRequest) {
        Integer classId = dataRequest.getInteger("classId");
        if (classId == null) {
            return CommonMethod.getReturnMessageError("classId不能为空");
        }

        List<Student> students = studentRepository.findAll();
        students = students.stream().filter(s ->
            s.getClassName() != null && s.getClassName().contains(classId.toString())
        ).toList();

        if (students.isEmpty()) {
            students = studentRepository.findAll();
            students = students.stream().limit(10).toList();
        }

        List<Map<String, Object>> classAnalysis = new ArrayList<>();
        for (Student student : students) {
            List<Score> scores = scoreRepository.findByStudentPersonId(student.getPersonId());
            if (!scores.isEmpty()) {
                Map<String, Object> analysis = analyzeStudentScores(scores, student.getPersonId());
                classAnalysis.add(analysis);
            }
        }

        return CommonMethod.getReturnData(classAnalysis);
    }

    public DataResponse getWarningStudents(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Integer threshold = dataRequest.getInteger("threshold");
        if (threshold == null) {
            threshold = 60;
        }

        List<Score> scores;
        if (courseId != null && courseId > 0) {
            scores = scoreRepository.findByStudentCourse(0, courseId);
        } else {
            scores = scoreRepository.findAll();
        }

        List<Map<String, Object>> warningList = new ArrayList<>();
        for (Score score : scores) {
            if (score.getMark() != null && score.getMark() < threshold) {
                Map<String, Object> warning = new HashMap<>();
                warning.put("scoreId", score.getScoreId());
                warning.put("studentId", score.getStudent().getPersonId());
                warning.put("studentName", score.getStudent().getPerson() != null ? score.getStudent().getPerson().getName() : "");
                warning.put("studentNum", score.getStudent().getPerson() != null ? score.getStudent().getPerson().getNum() : "");
                warning.put("courseName", score.getCourse() != null ? score.getCourse().getName() : "");
                warning.put("mark", score.getMark());
                warning.put("threshold", threshold);
                warning.put("gap", threshold - score.getMark());
                warningList.add(warning);
            }
        }

        return CommonMethod.getReturnData(warningList);
    }

    public DataResponse getOverallStatistics(DataRequest dataRequest) {
        List<Score> allScores = scoreRepository.findAll();

        if (allScores.isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("totalRecords", 0);
            emptyResult.put("avgScore", 0);
            return CommonMethod.getReturnData(emptyResult);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", allScores.size());

        double total = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int passCount = 0;

        for (Score score : allScores) {
            if (score.getMark() != null) {
                total += score.getMark();
                max = Math.max(max, score.getMark());
                min = Math.min(min, score.getMark());
                if (score.getMark() >= 60) {
                    passCount++;
                }
            }
        }

        stats.put("avgScore", allScores.size() > 0 ? Math.round(total / allScores.size() * 100.0) / 100.0 : 0);
        stats.put("maxScore", max == Integer.MIN_VALUE ? 0 : max);
        stats.put("minScore", min == Integer.MAX_VALUE ? 0 : min);
        stats.put("passRate", allScores.size() > 0 ? Math.round(passCount * 100.0 / allScores.size() * 100.0) / 100.0 : 0);

        return CommonMethod.getReturnData(stats);
    }

    private Map<String, Object> analyzeScores(List<Score> scores, Integer courseId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("courseId", courseId);
        analysis.put("totalStudents", scores.size());

        double total = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int passCount = 0;

        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("0-59", 0);
        distribution.put("60-69", 0);
        distribution.put("70-79", 0);
        distribution.put("80-89", 0);
        distribution.put("90-100", 0);

        for (Score score : scores) {
            if (score.getMark() != null) {
                int mark = score.getMark();
                total += mark;
                max = Math.max(max, mark);
                min = Math.min(min, mark);

                if (mark >= 60) {
                    passCount++;
                }

                if (mark < 60) {
                    distribution.put("0-59", distribution.get("0-59") + 1);
                } else if (mark < 70) {
                    distribution.put("60-69", distribution.get("60-69") + 1);
                } else if (mark < 80) {
                    distribution.put("70-79", distribution.get("70-79") + 1);
                } else if (mark < 90) {
                    distribution.put("80-89", distribution.get("80-89") + 1);
                } else {
                    distribution.put("90-100", distribution.get("90-100") + 1);
                }
            }
        }

        int validCount = (int) scores.stream().filter(s -> s.getMark() != null).count();
        analysis.put("avgScore", validCount > 0 ? Math.round(total / validCount * 100.0) / 100.0 : 0);
        analysis.put("maxScore", max == Integer.MIN_VALUE ? 0 : max);
        analysis.put("minScore", min == Integer.MAX_VALUE ? 0 : min);
        analysis.put("passCount", passCount);
        analysis.put("passRate", validCount > 0 ? Math.round(passCount * 100.0 / validCount * 100.0) / 100.0 : 0);
        analysis.put("distribution", distribution);

        return analysis;
    }

    private Map<String, Object> analyzeStudentScores(List<Score> scores, Integer studentId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("studentId", studentId);
        analysis.put("totalCourses", scores.size());

        double total = 0;
        int totalCredits = 0;
        int passedCourses = 0;
        int failedCourses = 0;

        for (Score score : scores) {
            if (score.getMark() != null) {
                total += score.getMark();
                if (score.getCourse() != null && score.getCourse().getCredit() != null) {
                    totalCredits += score.getCourse().getCredit();
                }
                if (score.getMark() >= 60) {
                    passedCourses++;
                } else {
                    failedCourses++;
                }
            }
        }

        int validCount = (int) scores.stream().filter(s -> s.getMark() != null).count();
        analysis.put("avgScore", validCount > 0 ? Math.round(total / validCount * 100.0) / 100.0 : 0);
        analysis.put("totalCredits", totalCredits);
        analysis.put("passedCourses", passedCourses);
        analysis.put("failedCourses", failedCourses);

        List<Map<String, Object>> courseDetails = new ArrayList<>();
        for (Score score : scores) {
            Map<String, Object> course = new HashMap<>();
            course.put("courseName", score.getCourse() != null ? score.getCourse().getName() : "");
            course.put("mark", score.getMark());
            course.put("credit", score.getCourse() != null ? score.getCourse().getCredit() : 0);
            course.put("passed", score.getMark() != null && score.getMark() >= 60);
            courseDetails.add(course);
        }
        analysis.put("courses", courseDetails);

        return analysis;
    }
}
