package com.cqu.admin.service.impl;

import com.cqu.common.dto.AdminCreateCourseRequest;
import com.cqu.common.dto.AdminCreateTeachingClassRequest;
import com.cqu.common.dto.StudentRegisterRequest;
import com.cqu.common.dto.TeacherRegisterRequest;
import com.cqu.common.entity.*;
import com.cqu.admin.mapper.*;
import com.cqu.admin.service.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
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
        // 检查学号是否已存在
        Map<String, Object> params = new HashMap<>();
        params.put("username", request.getStudentNo()); // 使用学号作为username
        List<User> existingUsers = userMapper.selectByMap(params);
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("学号已存在");
        }

        // 创建用户账号
        User user = new User();
        user.setUsername(request.getStudentNo()); // 使用学号作为用户名
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.STUDENT); // 使用枚举
        user.setRealName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);

        // 创建学生档案
        Student student = new Student();
        BeanUtils.copyProperties(request, student);
        student.setUserId(user.getId());
        student.setCreateTime(LocalDateTime.now());
        studentMapper.insert(student);

        return student;
    }

    @Override
    @Transactional
    public Teacher createTeacher(TeacherRegisterRequest request) {
        // 检查教工号是否已存在
        Map<String, Object> params = new HashMap<>();
        params.put("username", request.getTeacherNo()); // 使用教工号作为username
        List<User> existingUsers = userMapper.selectByMap(params);
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("教工号已存在");
        }

        // 创建用户账号
        User user = new User();
        user.setUsername(request.getTeacherNo()); // 使用教工号作为用户名
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.TEACHER); // 使用枚举
        user.setRealName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);

        // 创建教师档案
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(request, teacher);
        teacher.setUserId(user.getId());
        teacher.setCreateTime(LocalDateTime.now());
        teacherMapper.insert(teacher);

        return teacher;
    }

    @Override
    public Course createCourse(AdminCreateCourseRequest request) {
        // 检查课程编号是否已存在
        Map<String, Object> params = new HashMap<>();
        params.put("course_no", request.getCourseNo());
        List<Course> existingCourses = courseMapper.selectByMap(params);
        if (!existingCourses.isEmpty()) {
            throw new RuntimeException("课程编号已存在");
        }

        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        course.setCreateTime(LocalDateTime.now());
        courseMapper.insert(course);

        return course;
    }

    @Override
    public TeachingClass createTeachingClass(AdminCreateTeachingClassRequest request) {
        // 验证课程存在
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 验证教师存在
        Teacher teacher = teacherMapper.selectById(request.getTeacherId());
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }

        TeachingClass teachingClass = new TeachingClass();
        BeanUtils.copyProperties(request, teachingClass);
        teachingClass.setCurrentStudents(0); // 初始学生数为0
        teachingClass.setStatus(TeachingClass.STATUS_NOT_STARTED); // 默认未开课，可选课
        teachingClass.setCreateTime(LocalDateTime.now());
        teachingClassMapper.insert(teachingClass);

        return teachingClass;
    }

    @Override
    public List<Course> listCourses() {
        return courseMapper.selectList(null);
    }

    @Override
    public List<Teacher> listTeachers() {
        return teacherMapper.selectList(null);
    }

    @Override
    public List<Student> listStudents() {
        return studentMapper.selectList(null);
    }

    @Override
    public List<Map<String, Object>> listTeachingClasses() {
        List<TeachingClass> classes = teachingClassMapper.selectList(null);
        return classes.stream()
                .map(tc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("teachingClassId", tc.getId());
                    map.put("classNo", tc.getClassNo());
                    map.put("courseId", tc.getCourseId());
                    map.put("teacherId", tc.getTeacherId());
                    map.put("semester", tc.getSemester());
                    map.put("classroom", tc.getClassroom());
                    map.put("schedule", tc.getSchedule());
                    map.put("maxStudents", tc.getMaxStudents());
                    map.put("currentStudents", tc.getCurrentStudents());
                    map.put("status", tc.getStatus());

                    // 课程和教师详情需要调用其他服务获取
                    map.put("courseInfo", "请调用 course-service 获取课程详情");
                    map.put("teacherInfo", "请调用 teacher-service 获取教师详情");

                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 删除学生档案
        studentMapper.deleteById(studentId);

        // 删除用户账号
        if (student.getUserId() != null) {
            userMapper.deleteById(student.getUserId());
        }

        // 注意：实际应用中需要考虑是否删除相关的成绩记录等
        // 这里简化处理，只删除基本档案
    }

    @Override
    @Transactional
    public void deleteTeacher(Long teacherId) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }

        // 检查是否有正在进行的教学班
        Map<String, Object> params = new HashMap<>();
        params.put("teacher_id", teacherId);
        List<TeachingClass> classes = teachingClassMapper.selectByMap(params);
        if (!classes.isEmpty()) {
            throw new RuntimeException("该教师还有正在进行的教学班，无法删除");
        }

        // 删除教师档案
        teacherMapper.deleteById(teacherId);

        // 删除用户账号
        if (teacher.getUserId() != null) {
            userMapper.deleteById(teacher.getUserId());
        }
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 检查是否有相关的教学班
        Map<String, Object> params = new HashMap<>();
        params.put("course_id", courseId);
        List<TeachingClass> classes = teachingClassMapper.selectByMap(params);
        if (!classes.isEmpty()) {
            throw new RuntimeException("该课程还有关联的教学班，无法删除");
        }

        // 删除课程
        courseMapper.deleteById(courseId);
    }
}