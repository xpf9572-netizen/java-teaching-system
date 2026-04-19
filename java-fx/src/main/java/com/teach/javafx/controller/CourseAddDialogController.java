package com.teach.javafx.controller;

import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseAddDialogController {
    @FXML private TextField numField;
    @FXML private TextField nameField;
    @FXML private TextField creditField;
    @FXML private ComboBox<OptionItem> preCourseComboBox;

    private List<OptionItem> preCourseList;
    private CourseController courseController = null;

    @FXML
    public void initialize() {}

    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
    }

    public void init() {
        DataRequest req = new DataRequest();
        preCourseList = HttpRequestUtil.requestOptionItemList("/api/course/getCourseOptionList", req);
        if (preCourseList != null) {
            preCourseComboBox.getItems().addAll(preCourseList);
        }
    }

    @FXML
    public void okButtonClick() {
        Map<String, Object> data = new HashMap<>();
        data.put("num", numField.getText());
        data.put("name", nameField.getText());
        data.put("credit", creditField.getText());
        data.put("coursePath", "");

        OptionItem preCourse = preCourseComboBox.getSelectionModel().getSelectedItem();
        if (preCourse != null && preCourse.getValue() != null) {
            data.put("preCourseId", Integer.parseInt(preCourse.getValue()));
        }

        if (courseController != null) {
            courseController.doClose("ok", data);
        }
    }

    @FXML
    public void cancelButtonClick() {
        if (courseController != null) {
            courseController.doClose("cancel", null);
        }
    }
}
