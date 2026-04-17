package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentScoreController 学生成绩查看控制器
 * 学生查看自己的成绩列表
 */
public class StudentScoreController {

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> courseNameColumn;

    @FXML
    private TableColumn<Map, String> courseNumColumn;

    @FXML
    private TableColumn<Map, String> creditColumn;

    @FXML
    private TableColumn<Map, String> markColumn;

    @FXML
    private Label avgScoreLabel;

    @FXML
    private Label totalCoursesLabel;

    private ArrayList<Map> scoreList = new ArrayList<>();

    @FXML
    public void initialize() {
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));

        onQueryButtonClick();
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            DataRequest req = new DataRequest();
            DataResponse res = HttpRequestUtil.request("/api/student/getStudentIntroduceData", req);

            if (res != null && res.getCode() == 0) {
                Map<String, Object> data = (Map<String, Object>) res.getData();
                scoreList = (ArrayList<Map>) data.get("scoreList");

                // 更新统计
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

                // 更新表格
                setTableViewData();
            } else {
                MessageDialog.showDialog("获取成绩失败: " + res.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("查询失败: " + e.getMessage());
        }
    }

    private void setTableViewData() {
        dataTableView.getItems().clear();
        for (Map<String, Object> score : scoreList) {
            Map<String, Object> displayMap = new HashMap<>();
            displayMap.put("courseName", score.get("courseName"));
            displayMap.put("courseNum", score.get("courseNum"));
            displayMap.put("credit", score.get("credit"));

            Object markObj = score.get("mark");
            String markStr = markObj != null ? String.valueOf(((Number) markObj).intValue()) : "--";
            displayMap.put("mark", markStr);

            dataTableView.getItems().add(displayMap);
        }
    }
}
