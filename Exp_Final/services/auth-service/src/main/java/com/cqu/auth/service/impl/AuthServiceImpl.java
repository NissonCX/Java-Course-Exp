package com.cqu.auth.service.impl;

import com.cqu.common.dto.LoginRequest;
import com.cqu.common.entity.Student;
import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.User;
import com.cqu.auth.mapper.StudentMapper;
import com.cqu.auth.mapper.TeacherMapper;
import com.cqu.auth.mapper.UserMapper;
import com.cqu.security.JwtUtil;
import com.cqu.auth.service.AuthService;
import com.cqu.common.vo.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public Map<String, Object> getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("role", user.getRole().name());
        result.put("status", user.getStatus());

        return result;
    }

    @Override
    public void updateUserInfo(Long userId, String email, String phone) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (email != null) {
            user.setEmail(email);
        }
        if (phone != null) {
            user.setPhone(phone);
        }

        userMapper.updateById(user);
    }
}
