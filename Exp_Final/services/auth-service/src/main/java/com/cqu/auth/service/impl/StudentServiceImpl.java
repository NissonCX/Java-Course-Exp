package com.cqu.auth.service.impl;

import com.cqu.auth.mapper.StudentMapper;
import com.cqu.auth.mapper.UserMapper;
import com.cqu.auth.service.StudentService;
import com.cqu.common.dto.StudentRegisterRequest;
import com.cqu.common.entity.Student;
import com.cqu.common.entity.User;
import com.cqu.common.vo.LoginResponse;
import com.cqu.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学生服务实现类（auth-service 简化版本）
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

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
}
