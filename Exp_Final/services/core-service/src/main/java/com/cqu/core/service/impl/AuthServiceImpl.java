package com.cqu.core.service.impl;

import com.cqu.common.dto.LoginRequest;
import com.cqu.common.entity.Student;
import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.User;
import com.cqu.core.mapper.StudentMapper;
import com.cqu.core.mapper.TeacherMapper;
import com.cqu.core.mapper.UserMapper;
import com.cqu.core.security.JwtUtil;
import com.cqu.core.service.AuthService;
import com.cqu.common.vo.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        Long roleId = null;
        if (user.getRole() == User.UserRole.STUDENT) {
            Student student = studentMapper.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("学生信息不存在"));
            roleId = student.getId();
        } else if (user.getRole() == User.UserRole.TEACHER) {
            Teacher teacher = teacherMapper.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("教师信息不存在"));
            roleId = teacher.getId();
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getId(),
                roleId
        );

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole().name())
                .userId(user.getId())
                .roleId(roleId)
                .build();
    }
}
