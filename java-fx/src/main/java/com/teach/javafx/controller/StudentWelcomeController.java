package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * StudentWelcomeController 学生欢迎界面控制器
 * 显示学生个人信息、成绩概览、考勤情况等
 */
public class StudentWelcomeController {

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label studentNumLabel;

    @FXML
    private Label classNameLabel;

    @FXML
    private Label deptLabel;

    @FXML
    private Label avgScoreLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private VBox scoreListVBox;

    @FXML
    private VBox markListVBox;

    private Map<String, Object> studentInfo;
    private List<Map<String, Object>> scoreList;
    private List<Map<String, Object>> markList;

    @FXML
    public void initialize() {
        loadStudentData();
    }

    private void loadStudentData() {
        try {
            DataRequest req = new DataRequest();
            DataResponse res = HttpRequestUtil.request("/api/student/getStudentIntroduceData", req);

            if (res != null && res.getCode() == 0) {
                Map<String, Object> data = (Map<String, Object>) res.getData();
                studentInfo = (Map<String, Object>) data.get("info");
                scoreList = (List<Map<String, Object>>) data.get("scoreList");
                markList = (List<Map<String, Object>>) data.get("markList");

                // 显示学生基本信息
                if (studentInfo != null) {
                    studentNameLabel.setText((String) studentInfo.get("name"));
                    studentNumLabel.setText((String) studentInfo.get("num"));
                    classNameLabel.setText((String) studentInfo.get("className"));
                    deptLabel.setText((String) studentInfo.get("dept"));
                }

                // 显示成绩统计
                if (scoreList != null) {
                    totalCoursesLabel.setText(scoreList.size() + " 门课程");

                    double totalScore = 0;
                    int countWithScore = 0;
                    for (Map<String, Object> score : scoreList) {
                        Object markObj = score.get("mark");
                        if (markObj != null) {
                            double mark = ((Number) markObj).doubleValue();
                            if (mark >= 0) {
                                totalScore += mark;
                                countWithScore++;
                            }
                        }
                    }
                    if (countWithScore > 0) {
                        avgScoreLabel.setText(String.format("%.1f", totalScore / countWithScore));
                    } else {
                        avgScoreLabel.setText("--");
                    }
                }

                // 显示成绩列表（前5条）
                if (scoreList != null && !scoreList.isEmpty()) {
                    displayScoreList();
                }

                // 显示成绩分布
                if (markList != null && !markList.isEmpty()) {
                    displayMarkList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("加载数据失败: " + e.getMessage());
        }
    }

    private void displayScoreList() {
        scoreListVBox.getChildren().clear();

        int displayCount = Math.min(scoreList.size(), 5);
        for (int i = 0; i < displayCount; i++) {
            Map<String, Object> score = scoreList.get(i);
            Label label = new Label();
            String courseName = (String) score.get("courseName");
            Object markObj = score.get("mark");
            String markStr = markObj != null ? String.valueOf(((Number) markObj).intValue()) : "--";
            label.setText(courseName + "  :  " + markStr);
            label.setStyle("-fx-font-size: 14px; -fx-padding: 5 0 5 0;");
            scoreListVBox.getChildren().add(label);
        }

        if (scoreList.size() > 5) {
            Label moreLabel = new Label("... 共 " + scoreList.size() + " 门课程");
            moreLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            scoreListVBox.getChildren().add(moreLabel);
        }
    }

    private void displayMarkList() {
        markListVBox.getChildren().clear();

        for (Map<String, Object> item : markList) {
            String name = (String) item.get("name");
            Object valueObj = item.get("value");
            String value = valueObj != null ? valueObj.toString() : "0";
            Label label = new Label(name + " : " + value + " 门");
            label.setStyle("-fx-font-size: 14px; -fx-padding: 3 0 3 0;");
            markListVBox.getChildren().add(label);
        }
    }

    @FXML
    protected void onRefreshButtonClick() {
        loadStudentData();
        MessageDialog.showDialog("刷新成功");
    }
}
