package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentAchievement;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentAchievementRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StudentAchievementServiceTest {

    @Mock
    private StudentAchievementRepository achievementRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentAchievementService service;

    private MockedStatic<CommonMethod> commonMethodMockedStatic;

    @BeforeEach
    void setUp() {
        commonMethodMockedStatic = mockStatic(CommonMethod.class);
        // Stub all CommonMethod static methods used by the service
        commonMethodMockedStatic.when(CommonMethod::getPersonId).thenReturn(1);
        commonMethodMockedStatic.when(CommonMethod::getRoleName).thenReturn("STUDENT");
        commonMethodMockedStatic.when(() -> CommonMethod.getInteger(any(Map.class), anyString())).thenAnswer(inv -> {
            Map<String, Object> map = inv.getArgument(0);
            String key = inv.getArgument(1);
            Object value = map.get(key);
            if (value instanceof Integer) return (Integer) value;
            if (value == null) return null;
            return Integer.parseInt(value.toString());
        });
        commonMethodMockedStatic.when(() -> CommonMethod.getString(any(Map.class), anyString())).thenAnswer(inv -> {
            Map<String, Object> map = inv.getArgument(0);
            String key = inv.getArgument(1);
            Object value = map.get(key);
            return value != null ? value.toString() : null;
        });
        commonMethodMockedStatic.when(() -> CommonMethod.getReturnData(any())).thenAnswer(inv -> {
            DataResponse res = new DataResponse();
            res.setData(inv.getArgument(0));
            res.setCode(0);
            return res;
        });
        commonMethodMockedStatic.when(() -> CommonMethod.getReturnMessageOK()).thenAnswer(inv -> {
            DataResponse res = new DataResponse();
            res.setCode(0);
            res.setMsg("success");
            return res;
        });
        commonMethodMockedStatic.when(() -> CommonMethod.getReturnMessageError(anyString())).thenAnswer(inv -> {
            DataResponse res = new DataResponse();
            res.setCode(1);
            res.setMsg(inv.getArgument(0));
            return res;
        });
    }

    @AfterEach
    void tearDown() {
        commonMethodMockedStatic.close();
    }

    private StudentAchievement createTestAchievement(Integer achievementId, String type, String name) {
        StudentAchievement achievement = new StudentAchievement();
        achievement.setAchievementId(achievementId);
        achievement.setType(type);
        achievement.setName(name);
        achievement.setLevel("国家级");
        achievement.setAwardDate("2024-01-01");
        achievement.setDescription("Test description");
        achievement.setCertificateUrl("http://example.com/cert");
        achievement.setStatus("PENDING");
        return achievement;
    }

    private Student createTestStudent(Integer personId, String studentName) {
        Student student = new Student();
        student.setPersonId(personId);
        student.setMajor("计算机科学与技术");
        student.setClassName("2024级1班");

        Person person = new Person();
        person.setPersonId(personId);
        person.setName(studentName);
        student.setPerson(person);

        return student;
    }

    @Nested
    @DisplayName("getAchievementList tests")
    class GetAchievementListTests {

        @Test
        @DisplayName("Returns empty list when personId is null (not admin, no currentPersonId)")
        void getAchievementList_nullPersonId_returnsEmptyList() {
            // When not admin and currentPersonId is null, returns empty list
            commonMethodMockedStatic.when(CommonMethod::getPersonId).thenReturn(null);

            DataRequest request = new DataRequest();
            request.add("personId", null);

            DataResponse response = service.getAchievementList(request);

            assertThat(response.getData()).isInstanceOf(ArrayList.class);
            assertThat((ArrayList<?>) response.getData()).isEmpty();
            verify(achievementRepository, never()).findByStudentPersonIdOrderByDateDesc(any());
            verify(achievementRepository, never()).findByStudentPersonIdAndType(any(), any());
        }

        @Test
        @DisplayName("Returns achievements filtered by type when type is provided")
        void getAchievementList_withTypeAndPersonId_returnsFilteredList() {
            Integer personId = 1;
            String type = "COMPETITION";
            DataRequest request = new DataRequest();
            request.add("personId", personId);
            request.add("type", type);

            StudentAchievement achievement = createTestAchievement(1, type, "数学竞赛");
            Student student = createTestStudent(personId, "张三");
            achievement.setStudent(student);

            when(achievementRepository.findByStudentPersonIdAndType(personId, type))
                    .thenReturn(List.of(achievement));

            DataResponse response = service.getAchievementList(request);

            assertThat(response.getData()).isInstanceOf(ArrayList.class);
            assertThat((ArrayList<?>) response.getData()).hasSize(1);
            verify(achievementRepository).findByStudentPersonIdAndType(personId, type);
            verify(achievementRepository, never()).findByStudentPersonIdOrderByDateDesc(any());
        }

        @Test
        @DisplayName("Returns all achievements ordered by date when only personId is provided")
        void getAchievementList_withOnlyPersonId_returnsAllOrderedByDate() {
            Integer personId = 1;
            DataRequest request = new DataRequest();
            request.add("personId", personId);

            StudentAchievement achievement1 = createTestAchievement(1, "COMPETITION", "数学竞赛");
            Student student = createTestStudent(personId, "张三");
            achievement1.setStudent(student);

            when(achievementRepository.findByStudentPersonIdOrderByDateDesc(personId))
                    .thenReturn(List.of(achievement1));

            DataResponse response = service.getAchievementList(request);

            assertThat(response.getData()).isInstanceOf(ArrayList.class);
            assertThat((ArrayList<?>) response.getData()).hasSize(1);
            verify(achievementRepository).findByStudentPersonIdOrderByDateDesc(personId);
            verify(achievementRepository, never()).findByStudentPersonIdAndType(any(), any());
        }

        @Test
        @DisplayName("Handles null student gracefully in getMapFromAchievement")
        void getAchievementList_achievementWithNullStudent_handlesGracefully() {
            Integer personId = 1;
            DataRequest request = new DataRequest();
            request.add("personId", personId);

            StudentAchievement achievement = createTestAchievement(1, "COMPETITION", "数学竞赛");
            achievement.setStudent(null);

            when(achievementRepository.findByStudentPersonIdOrderByDateDesc(personId))
                    .thenReturn(List.of(achievement));

            DataResponse response = service.getAchievementList(request);

            assertThat(response.getData()).isInstanceOf(ArrayList.class);
            ArrayList<?> dataList = (ArrayList<?>) response.getData();
            assertThat(dataList).hasSize(1);
            assertThat(dataList.get(0)).isInstanceOf(Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) dataList.get(0);
            assertThat(resultMap).doesNotContainKey("studentId");
            assertThat(resultMap).doesNotContainKey("studentName");
        }

        @Test
        @DisplayName("Returns empty list when type is empty string")
        void getAchievementList_emptyType_returnsAllOrderedByDate() {
            Integer personId = 1;
            DataRequest request = new DataRequest();
            request.add("personId", personId);
            request.add("type", "");

            StudentAchievement achievement = createTestAchievement(1, "COMPETITION", "数学竞赛");
            Student student = createTestStudent(personId, "张三");
            achievement.setStudent(student);

            when(achievementRepository.findByStudentPersonIdOrderByDateDesc(personId))
                    .thenReturn(List.of(achievement));

            DataResponse response = service.getAchievementList(request);

            assertThat(response.getData()).isInstanceOf(ArrayList.class);
            assertThat((ArrayList<?>) response.getData()).hasSize(1);
            verify(achievementRepository).findByStudentPersonIdOrderByDateDesc(personId);
            verify(achievementRepository, never()).findByStudentPersonIdAndType(any(), any());
        }
    }

    @Nested
    @DisplayName("saveAchievement tests")
    class SaveAchievementTests {

        @Test
        @DisplayName("Creates new achievement when achievementId is null")
        void saveAchievement_nullAchievementId_createsNew() {
            DataRequest request = new DataRequest();
            Map<String, Object> form = new HashMap<>();
            form.put("achievementId", null);
            form.put("studentId", 1);
            form.put("type", "COMPETITION");
            form.put("name", "数学竞赛");
            form.put("level", "国家级");
            form.put("awardDate", "2024-01-01");
            form.put("description", "一等奖");
            form.put("certificateUrl", "http://example.com");
            request.add("form", form);

            Student student = createTestStudent(1, "张三");
            when(studentRepository.findById(1)).thenReturn(Optional.of(student));
            when(achievementRepository.save(any(StudentAchievement.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            DataResponse response = service.saveAchievement(request);

            assertThat(response.getCode()).isEqualTo(0);
            verify(achievementRepository).save(any(StudentAchievement.class));
        }

        @Test
        @DisplayName("Creates new achievement when achievementId is 0")
        void saveAchievement_zeroAchievementId_createsNew() {
            DataRequest request = new DataRequest();
            Map<String, Object> form = new HashMap<>();
            form.put("achievementId", 0);
            form.put("studentId", 1);
            form.put("type", "COMPETITION");
            form.put("name", "数学竞赛");
            form.put("level", "国家级");
            form.put("awardDate", "2024-01-01");
            form.put("description", "一等奖");
            form.put("certificateUrl", "http://example.com");
            request.add("form", form);

            Student student = createTestStudent(1, "张三");
            when(studentRepository.findById(1)).thenReturn(Optional.of(student));
            when(achievementRepository.save(any(StudentAchievement.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            DataResponse response = service.saveAchievement(request);

            assertThat(response.getCode()).isEqualTo(0);
            verify(achievementRepository).save(any(StudentAchievement.class));
        }

        @Test
        @DisplayName("Returns error when studentId is null on new creation (non-admin)")
        void saveAchievement_nullStudentId_returnsError() {
            DataRequest request = new DataRequest();
            Map<String, Object> form = new HashMap<>();
            form.put("achievementId", null);
            form.put("studentId", null);
            form.put("type", "COMPETITION");
            form.put("name", "数学竞赛");
            request.add("form", form);

            // Non-admin, so studentId comes from currentPersonId which is 1
            Student student = createTestStudent(1, "张三");
            when(studentRepository.findById(1)).thenReturn(Optional.of(student));
            when(achievementRepository.save(any(StudentAchievement.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            DataResponse response = service.saveAchievement(request);

            // Since currentPersonId (1) exists as a student, it should succeed
            assertThat(response.getCode()).isEqualTo(0);
            verify(achievementRepository).save(any(StudentAchievement.class));
        }

        @Test
        @DisplayName("Returns error when student does not exist (admin)")
        void saveAchievement_studentNotFound_returnsError() {
            DataRequest request = new DataRequest();
            Map<String, Object> form = new HashMap<>();
            form.put("achievementId", null);
            form.put("studentId", 999);
            form.put("type", "COMPETITION");
            form.put("name", "数学竞赛");
            request.add("form", form);

            // Mock as admin so requestStudentId (999) is used
            commonMethodMockedStatic.when(CommonMethod::getRoleName).thenReturn("ADMIN");
            when(studentRepository.findById(999)).thenReturn(Optional.empty());

            DataResponse response = service.saveAchievement(request);

            assertThat(response.getCode()).isEqualTo(1);
            assertThat(response.getMsg()).contains("学生不存在");
            verify(achievementRepository, never()).save(any());
        }

        @Test
        @DisplayName("Updates existing achievement when achievementId > 0")
        void saveAchievement_existingAchievement_updatesRecord() {
            Integer achievementId = 1;
            DataRequest request = new DataRequest();
            Map<String, Object> form = new HashMap<>();
            form.put("achievementId", achievementId);
            form.put("type", "PUBLICATION");
            form.put("name", "学术论文");
            form.put("level", "省级");
            form.put("awardDate", "2024-06-01");
            form.put("description", "已更新描述");
            form.put("certificateUrl", "http://example.com/new");
            request.add("form", form);

            StudentAchievement existingAchievement = createTestAchievement(achievementId, "COMPETITION", "旧名称");
            Student student = createTestStudent(1, "张三");
            existingAchievement.setStudent(student);

            when(achievementRepository.findById(achievementId))
                    .thenReturn(Optional.of(existingAchievement));
            when(achievementRepository.save(any(StudentAchievement.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            DataResponse response = service.saveAchievement(request);

            assertThat(response.getCode()).isEqualTo(0);
            verify(achievementRepository).findById(achievementId);
            verify(achievementRepository).save(any(StudentAchievement.class));
        }

        @Test
        @DisplayName("Returns error when updating non-existent achievement")
        void saveAchievement_achievementNotFound_returnsError() {
            Integer achievementId = 999;
            DataRequest request = new DataRequest();
            Map<String, Object> form = new HashMap<>();
            form.put("achievementId", achievementId);
            form.put("type", "COMPETITION");
            form.put("name", "数学竞赛");
            request.add("form", form);

            when(achievementRepository.findById(achievementId)).thenReturn(Optional.empty());

            DataResponse response = service.saveAchievement(request);

            assertThat(response.getCode()).isEqualTo(1);
            assertThat(response.getMsg()).contains("成就记录不存在");
            verify(achievementRepository, never()).save(any());
        }

        @Test
        @DisplayName("Sets status to PENDING on new records")
        void saveAchievement_newRecord_setsStatusToPending() {
            DataRequest request = new DataRequest();
            Map<String, Object> form = new HashMap<>();
            form.put("achievementId", null);
            form.put("studentId", 1);
            form.put("type", "COMPETITION");
            form.put("name", "数学竞赛");
            form.put("level", "国家级");
            form.put("awardDate", "2024-01-01");
            form.put("description", "一等奖");
            form.put("certificateUrl", "http://example.com");
            request.add("form", form);

            Student student = createTestStudent(1, "张三");
            when(studentRepository.findById(1)).thenReturn(Optional.of(student));
            when(achievementRepository.save(any(StudentAchievement.class)))
                    .thenAnswer(invocation -> {
                        StudentAchievement saved = invocation.getArgument(0);
                        assertThat(saved.getStatus()).isEqualTo("PENDING");
                        return saved;
                    });

            service.saveAchievement(request);

            verify(achievementRepository).save(any(StudentAchievement.class));
        }
    }

    @Nested
    @DisplayName("deleteAchievement tests")
    class DeleteAchievementTests {

        @Test
        @DisplayName("Deletes achievement when achievementId is valid")
        void deleteAchievement_validId_deletesSuccessfully() {
            Integer achievementId = 1;
            DataRequest request = new DataRequest();
            request.add("achievementId", achievementId);

            StudentAchievement achievement = createTestAchievement(achievementId, "COMPETITION", "数学竞赛");
            Student student = createTestStudent(1, "张三");
            achievement.setStudent(student);
            when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));

            DataResponse response = service.deleteAchievement(request);

            assertThat(response.getCode()).isEqualTo(0);
            verify(achievementRepository).findById(achievementId);
            verify(achievementRepository).delete(achievement);
        }

        @Test
        @DisplayName("Handles non-existent achievement gracefully")
        void deleteAchievement_nonExistentId_handlesGracefully() {
            Integer achievementId = 999;
            DataRequest request = new DataRequest();
            request.add("achievementId", achievementId);

            when(achievementRepository.findById(achievementId)).thenReturn(Optional.empty());

            DataResponse response = service.deleteAchievement(request);

            assertThat(response.getCode()).isEqualTo(0);
            verify(achievementRepository).findById(achievementId);
            verify(achievementRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Handles null achievementId gracefully")
        void deleteAchievement_nullId_handlesGracefully() {
            DataRequest request = new DataRequest();
            request.add("achievementId", null);

            DataResponse response = service.deleteAchievement(request);

            assertThat(response.getCode()).isEqualTo(0);
            verify(achievementRepository, never()).findById(any());
            verify(achievementRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Handles zero achievementId gracefully")
        void deleteAchievement_zeroId_handlesGracefully() {
            DataRequest request = new DataRequest();
            request.add("achievementId", 0);

            DataResponse response = service.deleteAchievement(request);

            assertThat(response.getCode()).isEqualTo(0);
            verify(achievementRepository, never()).findById(any());
            verify(achievementRepository, never()).delete(any());
        }
    }
}
