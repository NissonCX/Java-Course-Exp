package com.cqu.exp04.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqu.exp04.dto.AdminCreateCourseRequest;
import com.cqu.exp04.dto.AdminCreateTeachingClassRequest;
import com.cqu.exp04.dto.StudentRegisterRequest;
import com.cqu.exp04.dto.TeacherRegisterRequest;
import com.cqu.exp04.entity.Course;
import com.cqu.exp04.entity.Student;
import com.cqu.exp04.entity.Teacher;
import com.cqu.exp04.entity.TeachingClass;
import com.cqu.exp04.entity.User;
import com.cqu.exp04.mapper.CourseMapper;
import com.cqu.exp04.mapper.StudentMapper;
import com.cqu.exp04.mapper.TeacherMapper;
import com.cqu.exp04.mapper.TeachingClassMapper;
import com.cqu.exp04.mapper.UserMapper;
import com.cqu.exp04.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 超级管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeachingClassMapper teachingClassMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Student createStudent(StudentRegisterRequest request) {
        if (studentMapper.findByStudentNo(request.getStudentNo()).isPresent()) {
            throw new RuntimeException("学号已存在");
        }
        if (userMapper.findByUsername(request.getStudentNo()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

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

        return student;
    }

    @Override
    @Transactional
    public Teacher createTeacher(TeacherRegisterRequest request) {
        if (teacherMapper.findByTeacherNo(request.getTeacherNo()).isPresent()) {
            throw new RuntimeException("教工号已存在");
        }
        if (userMapper.findByUsername(request.getTeacherNo()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

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

        return teacher;
    }

    @Override
    @Transactional
    public Course createCourse(AdminCreateCourseRequest request) {
        if (courseMapper.findByCourseNo(request.getCourseNo()).isPresent()) {
            throw new RuntimeException("课程编号已存在");
        }

        Course course = Course.builder()
                .courseNo(request.getCourseNo())
                .courseName(request.getCourseName())
                .credit(request.getCredit())
                .hours(request.getHours())
                .courseType(request.getCourseType())
                .description(request.getDescription())
                .build();
        courseMapper.insert(course);
        return course;
    }

    @Override
    @Transactional
    public TeachingClass createTeachingClass(AdminCreateTeachingClassRequest request) {
        courseMapper.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        teacherMapper.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("教师不存在"));

        TeachingClass existing = teachingClassMapper.selectOne(
                new QueryWrapper<TeachingClass>()
                        .eq("class_no", request.getClassNo())
                        .eq("semester", request.getSemester())
        );
        if (existing != null) {
            throw new RuntimeException("该学期教学班编号已存在");
        }

        Integer status = request.getStatus() == null ? TeachingClass.STATUS_NOT_STARTED : request.getStatus();

        TeachingClass teachingClass = TeachingClass.builder()
                .classNo(request.getClassNo())
                .courseId(request.getCourseId())
                .teacherId(request.getTeacherId())
                .semester(request.getSemester())
                .maxStudents(request.getMaxStudents())
                .currentStudents(0)
                .classroom(request.getClassroom())
                .schedule(request.getSchedule())
                .status(status)
                .build();

        teachingClassMapper.insert(teachingClass);
        return teachingClass;
    }

    @Override
    public List<Course> listCourses() {
        return courseMapper.findAll();
    }

    @Override
    public List<Teacher> listTeachers() {
        return teacherMapper.findAll();
    }
}
