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
            DataResponse res = HttpRequestUtil.request("/api/teachers/" + teacherId, req);

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
            req.add("personId", teacherId);
            DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);

            if (res != null && res.getCode() == 0) {
                List<Map<String, Object>> courseList = (List<Map<String, Object>>) res.getData();
                if (courseList != null) {
                    totalCoursesLabel.setText(courseList.size() + " 门课程");
                    // 统计选课学生数（这里简化处理）
                    totalStudentsLabel.setText("-- 人");
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
