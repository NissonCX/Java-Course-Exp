package com.cqu.exp04.service.impl;

import com.cqu.exp04.dto.StudentRegisterRequest;
import com.cqu.exp04.entity.Enrollment;
import com.cqu.exp04.entity.Score;
import com.cqu.exp04.entity.Student;
import com.cqu.exp04.entity.TeachingClass;
import com.cqu.exp04.entity.User;
import com.cqu.exp04.mapper.EnrollmentMapper;
import com.cqu.exp04.mapper.ScoreMapper;
import com.cqu.exp04.mapper.StudentMapper;
import com.cqu.exp04.mapper.TeachingClassMapper;
import com.cqu.exp04.mapper.UserMapper;
import com.cqu.exp04.security.JwtUtil;
import com.cqu.exp04.service.AIService;
import com.cqu.exp04.service.StudentService;
import com.cqu.exp04.vo.LoginResponse;
import com.cqu.exp04.vo.StudentScoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Autowired
    private TeachingClassMapper teachingClassMapper;

    @Autowired
    private AIService aiService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public LoginResponse register(StudentRegisterRequest request) {
        // 1. 检查学号是否已存在
        if (studentMapper.findByStudentNo(request.getStudentNo()).isPresent()) {
            throw new RuntimeException("学号已存在");
        }

        // 2. 检查用户名是否已存在
        if (userMapper.findByUsername(request.getStudentNo()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 3. 创建User
        User user = User.builder()
                .username(request.getStudentNo())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.UserRole.STUDENT)
                .realName(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(1)
                .build();
        userMapper.insert(user);

        // 4. 创建Student
        Student student = Student.builder()
                .studentNo(request.getStudentNo())
                .userId(user.getId())
                .name(request.getName())
                .gender(Student.Gender.valueOf(request.getGender()))
                .birthDate(request.getBirthDate())
                .major(request.getMajor())
                .className(request.getClassName())
                .grade(request.getGrade())
                .enrollmentYear(request.getEnrollmentYear())
                .build();
        studentMapper.insert(student);

        // 5. 生成JWT token
        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getId(),
                student.getId()
        );

        // 6. 返回登录响应
        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole().name())
                .userId(user.getId())
                .roleId(student.getId())
                .build();
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        return studentMapper.findByStudentNo(studentNo)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }

    @Override
    public Student getById(Long id) {
        return studentMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }

    @Override
    public Map<String, Object> getMyScores(Long studentId) {
        // 1. 查询所有成绩
        List<Score> scores = scoreMapper.findByStudentId(studentId);
        List<StudentScoreVO> scoreDetails = scoreMapper.findStudentScoreDetails(studentId);

        // 2. 统计数据
        Map<String, Object> result = new HashMap<>();
        result.put("scores", scoreDetails);

        if (!scores.isEmpty()) {
            // 计算平均分(只计算有总分的课程)
            List<Score> completedScores = scores.stream()
                    .filter(s -> s.getTotalScore() != null)
                    .toList();

            if (!completedScores.isEmpty()) {
                BigDecimal totalScore = completedScores.stream()
                        .map(Score::getTotalScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal averageScore = totalScore.divide(
                        BigDecimal.valueOf(completedScores.size()),
                        2,
                        RoundingMode.HALF_UP
                );
                result.put("averageScore", averageScore);

                // 计算GPA(只计算有绩点的课程)
                BigDecimal totalGPA = completedScores.stream()
                        .map(Score::getGradePoint)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal gpa = totalGPA.divide(
                        BigDecimal.valueOf(completedScores.size()),
                        2,
                        RoundingMode.HALF_UP
                );
                result.put("gpa", gpa);

                // 统计课程数量
                result.put("completedCourses", completedScores.size());
                result.put("totalCourses", scores.size());

                // 统计及格/不及格
                long passedCount = completedScores.stream()
                        .filter(s -> s.getTotalScore().compareTo(BigDecimal.valueOf(60)) >= 0)
                        .count();
                result.put("passedCount", passedCount);
                result.put("failedCount", completedScores.size() - passedCount);
            } else {
                result.put("averageScore", 0);
                result.put("gpa", 0);
                result.put("completedCourses", 0);
                result.put("totalCourses", scores.size());
                result.put("passedCount", 0);
                result.put("failedCount", 0);
            }
        } else {
            result.put("averageScore", 0);
            result.put("gpa", 0);
            result.put("completedCourses", 0);
            result.put("totalCourses", 0);
            result.put("passedCount", 0);
            result.put("failedCount", 0);
        }

        return result;
    }

    @Override
    public List<StudentScoreVO> getMyScoreDetails(Long studentId) {
        return scoreMapper.findStudentScoreDetails(studentId);
    }

    @Override
    public String aiConsult(Long studentId, String message) {
        return aiService.studentConsult(studentId, message);
    }

    @Override
    @Transactional
    public void enrollCourse(Long studentId, Long teachingClassId) {
        // 1. 检查教学班是否存在
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(teachingClassId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        // 2. 检查教学班是否允许选课（只允许“未开课”状态选课）
        if (!teachingClass.isSelectableForEnrollment()) {
            throw new RuntimeException("该教学班" + teachingClass.getStatusText() + "，当前不允许选课");
        }

        // 3. 检查是否已选课
        if (enrollmentMapper.findByStudentAndClass(studentId, teachingClassId).isPresent()) {
            throw new RuntimeException("您已经选过这门课了");
        }

        // 4. 检查教学班是否已满
        if (teachingClass.getCurrentStudents() >= teachingClass.getMaxStudents()) {
            throw new RuntimeException("教学班已满,无法选课");
        }

        // 5. 创建选课记录
        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .teachingClassId(teachingClassId)
                .enrollTime(LocalDateTime.now())
                .status(1)  // 1-正常
                .build();
        enrollmentMapper.insert(enrollment);

        // 5. 更新教学班当前人数（并发安全：未满员才 +1）
        int updated = teachingClassMapper.incrementCurrentStudentsIfNotFull(teachingClassId);
        if (updated != 1) {
            throw new RuntimeException("教学班已满,无法选课");
        }
    }

    @Override
    public List<Map<String, Object>> getMyEnrollments(Long studentId) {
        List<Enrollment> enrollments = enrollmentMapper.findByStudentId(studentId);

        return enrollments.stream().map(enrollment -> {
            Map<String, Object> map = new HashMap<>();

            // 获取教学班详情
            TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(enrollment.getTeachingClassId())
                    .orElse(null);

            if (teachingClass != null) {
                map.put("enrollmentId", enrollment.getId());
                map.put("teachingClassId", teachingClass.getId());
                map.put("classNo", teachingClass.getClassNo());
                map.put("courseName", teachingClass.getCourse().getCourseName());
                map.put("courseNo", teachingClass.getCourse().getCourseNo());
                map.put("credit", teachingClass.getCourse().getCredit());
                map.put("teacherName", teachingClass.getTeacher().getName());
                map.put("semester", teachingClass.getSemester());
                map.put("classroom", teachingClass.getClassroom());
                map.put("schedule", teachingClass.getSchedule());
                map.put("enrollTime", enrollment.getEnrollTime());
                map.put("status", enrollment.getStatus());

                // 返回教学班状态，便于前端判断是否允许退课
                map.put("teachingClassStatus", teachingClass.getStatus());
                map.put("teachingClassStatusText", teachingClass.getStatusText());

                // 查询是否有成绩
                scoreMapper.findByEnrollmentId(enrollment.getId()).ifPresent(score -> {
                    map.put("hasScore", true);
                    map.put("totalScore", score.getTotalScore());
                    map.put("gradePoint", score.getGradePoint());
                });

                if (!map.containsKey("hasScore")) {
                    map.put("hasScore", false);
                }
            }

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void dropCourse(Long studentId, Long enrollmentId) {
        // 1. 查询选课记录
        Enrollment enrollment = enrollmentMapper.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("选课记录不存在"));

        // 2. 验证是否是该学生的选课记录
        if (!enrollment.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权限退课");
        }

        // 3. 检查是否已有成绩(有成绩不允许退课)
        if (scoreMapper.findByEnrollmentId(enrollmentId).isPresent()) {
            throw new RuntimeException("已录入成绩的课程无法退课");
        }

        // 4. 获取教学班信息
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(enrollment.getTeachingClassId())
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        // 5. 已开课/已结课 不允许退课（只允许“未开课”退课）
        if (!teachingClass.isSelectableForEnrollment()) {
            throw new RuntimeException("该教学班" + teachingClass.getStatusText() + "，当前不允许退课");
        }

        // 6. 删除选课记录
        enrollmentMapper.deleteById(enrollmentId);

        // 7. 更新教学班当前人数
        teachingClassMapper.updateCurrentStudents(
                enrollment.getTeachingClassId(),
                teachingClass.getCurrentStudents() - 1
        );
    }

    @Override
    @Transactional
    public void updateProfile(Long studentId, Student student) {
        // 1. 验证学生是否存在
        Student existingStudent = studentMapper.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        // 2. 更新允许修改的字段
        existingStudent.setName(student.getName());
        existingStudent.setGender(student.getGender());
        existingStudent.setBirthDate(student.getBirthDate());
        existingStudent.setMajor(student.getMajor());
        existingStudent.setClassName(student.getClassName());

        // 3. 执行更新
        studentMapper.updateById(existingStudent);

        // 4. 同步更新User表的realName
        User user = userMapper.findById(existingStudent.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setRealName(student.getName());
        userMapper.updateById(user);
    }
}
