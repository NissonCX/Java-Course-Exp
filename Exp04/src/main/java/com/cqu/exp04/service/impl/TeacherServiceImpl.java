package com.cqu.exp04.service.impl;

import com.cqu.exp04.dto.ScoreInputRequest;
import com.cqu.exp04.dto.TeacherRegisterRequest;
import com.cqu.exp04.entity.Score;
import com.cqu.exp04.entity.Teacher;
import com.cqu.exp04.entity.TeachingClass;
import com.cqu.exp04.entity.User;
import com.cqu.exp04.mapper.EnrollmentMapper;
import com.cqu.exp04.mapper.ScoreMapper;
import com.cqu.exp04.mapper.TeacherMapper;
import com.cqu.exp04.mapper.TeachingClassMapper;
import com.cqu.exp04.mapper.UserMapper;
import com.cqu.exp04.security.JwtUtil;
import com.cqu.exp04.service.AIService;
import com.cqu.exp04.service.ScoreService;
import com.cqu.exp04.service.TeacherService;
import com.cqu.exp04.vo.ClassScoreStatisticsVO;
import com.cqu.exp04.vo.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeachingClassMapper teachingClassMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private AIService aiService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public LoginResponse register(TeacherRegisterRequest request) {
        // 1. 检查教工号是否已存在
        if (teacherMapper.findByTeacherNo(request.getTeacherNo()).isPresent()) {
            throw new RuntimeException("教工号已存在");
        }

        // 2. 检查用户名是否已存在
        if (userMapper.findByUsername(request.getTeacherNo()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 3. 创建User
        User user = User.builder()
                .username(request.getTeacherNo())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.UserRole.TEACHER)
                .realName(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(1)
                .build();
        userMapper.insert(user);

        // 4. 创建Teacher
        Teacher teacher = Teacher.builder()
                .teacherNo(request.getTeacherNo())
                .userId(user.getId())
                .name(request.getName())
                .gender(Teacher.Gender.valueOf(request.getGender()))
                .title(request.getTitle())
                .department(request.getDepartment())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
        teacherMapper.insert(teacher);

        // 5. 生成JWT token
        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getId(),
                teacher.getId()
        );

        // 6. 返回登录响应
        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole().name())
                .userId(user.getId())
                .roleId(teacher.getId())
                .build();
    }

    @Override
    public Teacher getByTeacherNo(String teacherNo) {
        return teacherMapper.findByTeacherNo(teacherNo)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
    }

    @Override
    public Teacher getById(Long id) {
        return teacherMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
    }

    @Override
    public List<TeachingClass> getMyClasses(Long teacherId) {
        return teachingClassMapper.findByTeacherId(teacherId);
    }

    @Override
    public List<Map<String, Object>> getClassStudents(Long teacherId, Long teachingClassId) {
        // 1. 验证权限
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(teachingClassId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        if (!teachingClass.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限查看此教学班");
        }

        // 2. 查询教学班的所有选课记录
        return enrollmentMapper.findByTeachingClassId(teachingClassId).stream()
                .map(enrollment -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("enrollmentId", enrollment.getId());
                    map.put("studentId", enrollment.getStudentId());
                    map.put("studentNo", enrollment.getStudent().getStudentNo());
                    map.put("studentName", enrollment.getStudent().getName());
                    map.put("gender", enrollment.getStudent().getGender());
                    map.put("major", enrollment.getStudent().getMajor());
                    map.put("className", enrollment.getStudent().getClassName());
                    map.put("enrollTime", enrollment.getEnrollTime());

                    // 查询成绩
                    scoreMapper.findByEnrollmentId(enrollment.getId()).ifPresentOrElse(
                            score -> {
                                map.put("hasScore", true);
                                map.put("usualScore", score.getUsualScore());
                                map.put("midtermScore", score.getMidtermScore());
                                map.put("experimentScore", score.getExperimentScore());
                                map.put("finalScore", score.getFinalScore());
                                map.put("totalScore", score.getTotalScore());
                                map.put("gradePoint", score.getGradePoint());
                            },
                            () -> map.put("hasScore", false)
                    );

                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Score> getClassScores(Long teacherId, Long teachingClassId) {
        // 1. 验证权限
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(teachingClassId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        if (!teachingClass.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限查看此教学班");
        }

        // 2. 查询成绩
        return scoreMapper.findByTeachingClassId(teachingClassId);
    }

    @Override
    @Transactional
    public void inputScore(Long teacherId, ScoreInputRequest request) {
        // 使用ScoreService统一处理成绩录入逻辑
        scoreService.inputScore(teacherId, request);
    }

    @Override
    @Transactional
    public void batchInputScores(Long teacherId, Long teachingClassId, List<ScoreInputRequest> requests) {
        // 1. 验证权限
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(teachingClassId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        if (!teachingClass.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限操作此教学班");
        }

        // 2. 批量录入成绩
        for (ScoreInputRequest request : requests) {
            request.setTeachingClassId(teachingClassId);
            scoreService.inputScore(teacherId, request);
        }
    }

    @Override
    public ClassScoreStatisticsVO getClassStatistics(Long teacherId, Long teachingClassId) {
        // 1. 验证权限
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(teachingClassId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        if (!teachingClass.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限查看此教学班");
        }

        // 2. 获取统计数据
        return scoreService.getClassStatistics(teachingClassId);
    }

    @Override
    public String aiConsult(Long teacherId, Long teachingClassId, String message) {
        // 1. 验证权限
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(teachingClassId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        if (!teachingClass.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限咨询此教学班");
        }

        // 2. 调用AI服务
        return aiService.teacherConsult(teacherId, teachingClassId, message);
    }

    @Override
    @Transactional
    public void updateTeachingClassStatus(Long teacherId, Long teachingClassId, Integer status) {
        // 1. 参数校验
        if (status == null || !(status == TeachingClass.STATUS_NOT_STARTED
                || status == TeachingClass.STATUS_IN_PROGRESS
                || status == TeachingClass.STATUS_FINISHED)) {
            throw new RuntimeException("无效的教学班状态");
        }

        // 2. 验证权限
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(teachingClassId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        if (!teachingClass.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限操作此教学班");
        }

        // 3. 状态流转约束：已结课不可重新开课
        Integer currentStatus = teachingClass.getStatus();
        if (currentStatus != null && currentStatus == TeachingClass.STATUS_FINISHED
                && status != TeachingClass.STATUS_FINISHED) {
            throw new RuntimeException("已结课的教学班不可重新开课");
        }

        // 4. 执行更新
        TeachingClass update = new TeachingClass();
        update.setId(teachingClassId);
        update.setStatus(status);
        teachingClassMapper.update(update);
    }

    @Override
    @Transactional
    public void updateProfile(Long teacherId, Teacher teacher) {
        // 1. 验证教师是否存在
        Teacher existingTeacher = teacherMapper.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("教师不存在"));

        // 2. 更新允许修改的字段
        existingTeacher.setName(teacher.getName());
        existingTeacher.setGender(teacher.getGender());
        existingTeacher.setTitle(teacher.getTitle());
        existingTeacher.setDepartment(teacher.getDepartment());
        existingTeacher.setEmail(teacher.getEmail());
        existingTeacher.setPhone(teacher.getPhone());

        // 3. 执行更新
        teacherMapper.update(existingTeacher);

        // 4. 同步更新User表的realName
        User user = userMapper.findById(existingTeacher.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setRealName(teacher.getName());
        userMapper.update(user);
    }
}
