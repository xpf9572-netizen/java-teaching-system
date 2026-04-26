package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ImportExportService {
    private final StudentRepository studentRepository;
    private final ScoreRepository scoreRepository;
    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    public ImportExportService(StudentRepository studentRepository,
                              ScoreRepository scoreRepository,
                              CourseRepository courseRepository,
                              PersonRepository personRepository,
                              UserRepository userRepository,
                              ExamRepository examRepository) {
        this.studentRepository = studentRepository;
        this.scoreRepository = scoreRepository;
        this.courseRepository = courseRepository;
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
    }

    public ResponseEntity<StreamingResponseBody> exportStudentRoster() {
        List<Student> students = studentRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生名册");

            CellStyle headerStyle = CommonMethod.createCellStyle(workbook, 12);
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "性别", "班级", "专业", "学院", "出生日期", "电话", "地址", "邮箱"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                Person person = student.getPerson();
                if (person != null) {
                    row.createCell(0).setCellValue(person.getNum() != null ? person.getNum() : "");
                    row.createCell(1).setCellValue(person.getName() != null ? person.getName() : "");
                    row.createCell(2).setCellValue("1".equals(person.getGender()) ? "男" : "女");
                    row.createCell(3).setCellValue(student.getClassName() != null ? student.getClassName() : "");
                    row.createCell(4).setCellValue(student.getMajor() != null ? student.getMajor() : "");
                    row.createCell(5).setCellValue(person.getDept() != null ? person.getDept() : "");
                    row.createCell(6).setCellValue(person.getBirthday() != null ? person.getBirthday() : "");
                    row.createCell(7).setCellValue(person.getPhone() != null ? person.getPhone() : "");
                    row.createCell(8).setCellValue(person.getAddress() != null ? person.getAddress() : "");
                    row.createCell(9).setCellValue(person.getEmail() != null ? person.getEmail() : "");
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final byte[] data = out.toByteArray();
        StreamingResponseBody stream = outputStream -> outputStream.write(data);
        return ResponseEntity.ok()
                .contentType(CommonMethod.exelType)
                .header("Content-Disposition", "attachment; filename=student_roster.xlsx")
                .body(stream);
    }

    public ResponseEntity<StreamingResponseBody> exportScoreRecords(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        List<Score> scores;

        if (courseId != null && courseId > 0) {
            scores = scoreRepository.findByStudentCourse(0, courseId);
        } else {
            scores = scoreRepository.findAll();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩记录");

            CellStyle headerStyle = CommonMethod.createCellStyle(workbook, 12);
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "课程编号", "课程名称", "学分", "成绩", "等级"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Score score : scores) {
                Row row = sheet.createRow(rowNum++);
                Student student = score.getStudent();
                Course course = score.getCourse();

                row.createCell(0).setCellValue(student != null && student.getPerson() != null && student.getPerson().getNum() != null ? student.getPerson().getNum() : "");
                row.createCell(1).setCellValue(student != null && student.getPerson() != null ? student.getPerson().getName() : "");
                row.createCell(2).setCellValue(student != null ? student.getClassName() : "");
                row.createCell(3).setCellValue(course != null ? course.getNum() : "");
                row.createCell(4).setCellValue(course != null ? course.getName() : "");
                row.createCell(5).setCellValue(course != null && course.getCredit() != null ? course.getCredit() : 0);
                row.createCell(6).setCellValue(score.getMark() != null ? score.getMark() : 0);
                row.createCell(7).setCellValue(score.getMark() != null ? CommonMethod.getLevelFromScore(score.getMark().doubleValue()) : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final byte[] data = out.toByteArray();
        StreamingResponseBody stream = outputStream -> outputStream.write(data);
        return ResponseEntity.ok()
                .contentType(CommonMethod.exelType)
                .header("Content-Disposition", "attachment; filename=score_records.xlsx")
                .body(stream);
    }

    public ResponseEntity<StreamingResponseBody> exportExamArrangements(DataRequest dataRequest) {
        List<Exam> exams = examRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("考试安排");

            CellStyle headerStyle = CommonMethod.createCellStyle(workbook, 12);
            Row headerRow = sheet.createRow(0);
            String[] headers = {"课程名称", "学期", "考试日期", "考试时间", "考试地点", "监考教师", "考试类型", "报考人数"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Exam exam : exams) {
                Row row = sheet.createRow(rowNum++);
                Course course = exam.getCourse();
                Teacher invigilator = exam.getInvigilator();

                row.createCell(0).setCellValue(course != null ? course.getName() : "");
                row.createCell(1).setCellValue(exam.getSemester() != null ? exam.getSemester() : "");
                row.createCell(2).setCellValue(exam.getExamDate() != null ? sdf.format(exam.getExamDate()) : "");
                row.createCell(3).setCellValue(exam.getExamTime() != null ? exam.getExamTime() : "");
                row.createCell(4).setCellValue(exam.getExamLocation() != null ? exam.getExamLocation() : "");
                row.createCell(5).setCellValue(invigilator != null && invigilator.getPerson() != null ? invigilator.getPerson().getName() : "");
                row.createCell(6).setCellValue(exam.getExamType() != null ? exam.getExamType() : "");
                row.createCell(7).setCellValue(exam.getTotalStudents() != null ? exam.getTotalStudents() : 0);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final byte[] data = out.toByteArray();
        StreamingResponseBody stream = outputStream -> outputStream.write(data);
        return ResponseEntity.ok()
                .contentType(CommonMethod.exelType)
                .header("Content-Disposition", "attachment; filename=exam_arrangements.xlsx")
                .body(stream);
    }

    public DataResponse importStudents(InputStream inputStream, String fileName) {
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, Object> rowResult = new HashMap<>();
                rowResult.put("row", i + 1);

                try {
                    String num = getCellValueAsString(row.getCell(0));
                    String name = getCellValueAsString(row.getCell(1));
                    String gender = getCellValueAsString(row.getCell(2));
                    String className = getCellValueAsString(row.getCell(3));
                    String major = getCellValueAsString(row.getCell(4));
                    String dept = getCellValueAsString(row.getCell(5));
                    String birthday = getCellValueAsString(row.getCell(6));
                    String phone = getCellValueAsString(row.getCell(7));
                    String address = getCellValueAsString(row.getCell(8));
                    String email = getCellValueAsString(row.getCell(9));

                    if (num == null || num.isEmpty()) {
                        rowResult.put("status", "失败");
                        rowResult.put("message", "学号不能为空");
                        results.add(rowResult);
                        failCount++;
                        continue;
                    }

                    Optional<Person> existingPerson = personRepository.findByNum(num);
                    if (existingPerson.isPresent()) {
                        rowResult.put("status", "跳过");
                        rowResult.put("message", "学号已存在");
                        results.add(rowResult);
                        continue;
                    }

                    Person person = new Person();
                    person.setNum(num);
                    person.setName(name);
                    person.setGender("男".equals(gender) ? "1" : "2");
                    person.setDept(dept);
                    person.setBirthday(birthday);
                    person.setPhone(phone);
                    person.setAddress(address);
                    person.setEmail(email);
                    person.setType("1");
                    Person savedPerson = personRepository.save(person);

                    Student student = new Student();
                    student.setPersonId(savedPerson.getPersonId());
                    student.setPerson(savedPerson);
                    student.setClassName(className);
                    student.setMajor(major);
                    studentRepository.save(student);

                    rowResult.put("status", "成功");
                    rowResult.put("message", "导入成功");
                    results.add(rowResult);
                    successCount++;

                } catch (Exception e) {
                    rowResult.put("status", "失败");
                    rowResult.put("message", e.getMessage());
                    results.add(rowResult);
                    failCount++;
                }
            }
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("文件读取失败: " + e.getMessage());
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("total", successCount + failCount);
        summary.put("success", successCount);
        summary.put("fail", failCount);
        summary.put("details", results);

        return CommonMethod.getReturnData(summary);
    }

    public DataResponse importScores(InputStream inputStream, String fileName) {
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, Object> rowResult = new HashMap<>();
                rowResult.put("row", i + 1);

                try {
                    String studentNum = getCellValueAsString(row.getCell(0));
                    String courseNum = getCellValueAsString(row.getCell(1));
                    Object markObj = row.getCell(2);
                    int mark = 0;
                    if (markObj != null) {
                        if (markObj instanceof Cell) {
                            Cell c = (Cell) markObj;
                            if (c.getCellType() == CellType.NUMERIC) {
                                mark = (int) c.getNumericCellValue();
                            } else {
                                mark = Integer.parseInt(getCellValueAsString(markObj));
                            }
                        } else {
                            mark = Integer.parseInt(getCellValueAsString(markObj));
                        }
                    }

                    if (studentNum == null || studentNum.isEmpty()) {
                        rowResult.put("status", "失败");
                        rowResult.put("message", "学号不能为空");
                        results.add(rowResult);
                        failCount++;
                        continue;
                    }

                    Optional<Person> personOp = personRepository.findByNum(studentNum);
                    if (personOp.isEmpty()) {
                        rowResult.put("status", "失败");
                        rowResult.put("message", "学号不存在: " + studentNum);
                        results.add(rowResult);
                        failCount++;
                        continue;
                    }

                    List<Course> courses = courseRepository.findAll();
                    Course targetCourse = null;
                    for (Course c : courses) {
                        if (c.getNum().equals(courseNum)) {
                            targetCourse = c;
                            break;
                        }
                    }

                    if (targetCourse == null) {
                        rowResult.put("status", "失败");
                        rowResult.put("message", "课程编号不存在: " + courseNum);
                        results.add(rowResult);
                        failCount++;
                        continue;
                    }

                    Optional<Student> studentOp = studentRepository.findById(personOp.get().getPersonId());
                    if (studentOp.isEmpty()) {
                        rowResult.put("status", "失败");
                        rowResult.put("message", "学生不存在: " + studentNum);
                        results.add(rowResult);
                        failCount++;
                        continue;
                    }

                    Score score = new Score();
                    score.setStudent(studentOp.get());
                    score.setCourse(targetCourse);
                    score.setMark(mark);
                    scoreRepository.save(score);

                    rowResult.put("status", "成功");
                    rowResult.put("message", "导入成功");
                    results.add(rowResult);
                    successCount++;

                } catch (Exception e) {
                    rowResult.put("status", "失败");
                    rowResult.put("message", e.getMessage());
                    results.add(rowResult);
                    failCount++;
                }
            }
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("文件读取失败: " + e.getMessage());
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("total", successCount + failCount);
        summary.put("success", successCount);
        summary.put("fail", failCount);
        summary.put("details", results);

        return CommonMethod.getReturnData(summary);
    }

    private String getCellValueAsString(Object cell) {
        if (cell == null) return "";
        if (cell instanceof String) return (String) cell;
        if (cell instanceof Cell) {
            Cell c = (Cell) cell;
            switch (c.getCellType()) {
                case STRING: return c.getStringCellValue();
                case NUMERIC: return String.valueOf((int) c.getNumericCellValue());
                case BOOLEAN: return String.valueOf(c.getBooleanCellValue());
                default: return "";
            }
        }
        return cell.toString();
    }
}
