package com.teach.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.controller.base.MessageDialog;

public class ImportExportController {
    @FXML
    private ComboBox<String> exportTypeComboBox;
    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private void initialize() {
        exportTypeComboBox.getItems().addAll("学生名册", "成绩记录", "考试安排");
        exportTypeComboBox.setValue("学生名册");

        courseComboBox.getItems().add("全部课程");
        courseComboBox.setValue("全部课程");
    }

    @FXML
    private void onExportButtonClick() {
        String exportType = exportTypeComboBox.getValue();
        if (exportType == null) return;

        try {
            if ("学生名册".equals(exportType)) {
                HttpRequestUtil.downloadFile("/api/importExport/exportStudentRoster", "student_roster.xlsx");
                MessageDialog.showDialog("导出成功！");
            } else if ("成绩记录".equals(exportType)) {
                DataRequest req = new DataRequest();
                HttpRequestUtil.downloadFileWithRequest("/api/importExport/exportScoreRecords", req, "score_records.xlsx");
                MessageDialog.showDialog("导出成功！");
            } else if ("考试安排".equals(exportType)) {
                HttpRequestUtil.downloadFile("/api/importExport/exportExamArrangements", "exam_arrangements.xlsx");
                MessageDialog.showDialog("导出成功！");
            }
        } catch (Exception e) {
            MessageDialog.showDialog("导出失败: " + e.getMessage());
        }
    }

    @FXML
    private void onImportStudentButtonClick() {
        MessageDialog.showDialog("学生导入功能：请在弹出的文件选择器中选择Excel文件(.xlsx)");
    }

    @FXML
    private void onImportScoreButtonClick() {
        MessageDialog.showDialog("成绩导入功能：请在弹出的文件选择器中选择Excel文件(.xlsx)");
    }
}
