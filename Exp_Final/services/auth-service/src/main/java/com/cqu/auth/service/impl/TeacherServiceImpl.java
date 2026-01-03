package com.cqu.auth.service.impl;

import com.cqu.auth.mapper.TeacherMapper;
import com.cqu.auth.mapper.UserMapper;
import com.cqu.auth.service.TeacherService;
import com.cqu.common.dto.TeacherRegisterRequest;
import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.User;
import com.cqu.common.vo.LoginResponse;
import com.cqu.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 教师服务实现类（auth-service 简化版本）
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private UserMapper userMapper;

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
}
