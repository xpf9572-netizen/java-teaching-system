package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Map;

/**
 * WelcomeTeacherController 教师欢迎界面控制器
 * 显示教师个人信息、课程统计等
 */
public class WelcomeTeacherController {

    @FXML
    private Label teacherNameLabel;

    @FXML
    private Label teacherNumLabel;

    @FXML
    private Label deptLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label totalStudentsLabel;

    @FXML
    private Label totalAvgScoreLabel;

    @FXML
    private Label totalPassRateLabel;

    private Map<String, Object> teacherInfo;
    private List<Map<String, Object>> courseList;

    @FXML
    public void initialize() {
        loadTeacherData();
    }

    private void loadTeacherData() {
        try {
            // 获取当前登录用户的ID
            Integer teacherId = AppStore.getJwt().getId();

            DataRequest req = new DataRequest();
            req.add("personId", teacherId);
            DataResponse res = HttpRequestUtil.request("/api/teachers/getTeacherInfo", req);

            if (res != null && res.getCode() == 0) {
                teacherInfo = (Map<String, Object>) res.getData();

                // 显示教师基本信息
                if (teacherInfo != null) {
                    teacherNameLabel.setText((String) teacherInfo.get("name"));
                    teacherNumLabel.setText((String) teacherInfo.get("teacherNum"));
                    deptLabel.setText((String) teacherInfo.get("department"));
                    titleLabel.setText((String) teacherInfo.get("title"));
                }

                // 获取课程数量（通过另一个API）
                loadCourseStats();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("加载数据失败: " + e.getMessage());
        }
    }

    private void loadCourseStats() {
        try {
            Integer teacherId = AppStore.getJwt().getId();
            DataRequest req = new DataRequest();
            req.add("teacherId", teacherId);
            DataResponse res = HttpRequestUtil.request("/api/course/getCourseListByTeacher", req);

            if (res != null && res.getCode() == 0) {
                List<Map<String, Object>> courseList = (List<Map<String, Object>>) res.getData();
                if (courseList != null) {
                    totalCoursesLabel.setText(courseList.size() + " 门课程");
                    int totalStudentCount = 0;
                    for (Map<String, Object> course : courseList) {
                        Object count = course.get("studentCount");
                        if (count instanceof Number) {
                            totalStudentCount += ((Number) count).intValue();
                        }
                    }
                    totalStudentsLabel.setText(totalStudentCount + " 人");
                }
            }

            // 加载总体成绩统计
            DataResponse statsRes = HttpRequestUtil.request("/api/scoreAnalysis/getOverallStatistics", new DataRequest());
            if (statsRes != null && statsRes.getCode() == 0) {
                Map<String, Object> data = (Map<String, Object>) statsRes.getData();
                if (data != null) {
                    Object avgScore = data.get("avgScore");
                    totalAvgScoreLabel.setText(avgScore != null ? avgScore.toString() : "--");

                    Object passRate = data.get("passRate");
                    totalPassRateLabel.setText(passRate != null ? passRate.toString() + "%" : "--");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRefreshButtonClick() {
        loadTeacherData();
        MessageDialog.showDialog("刷新成功");
    }
}
