package com.cqu.admin.service;

import com.cqu.common.dto.AdminCreateCourseRequest;
import com.cqu.common.dto.AdminCreateTeachingClassRequest;
import com.cqu.common.dto.StudentRegisterRequest;
import com.cqu.common.dto.TeacherRegisterRequest;
import com.cqu.common.entity.Course;
import com.cqu.common.entity.Student;
import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.TeachingClass;

import java.util.List;
import java.util.Map;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 创建学生（用户 + 档案）
     */
    Student createStudent(StudentRegisterRequest request);

    /**
     * 创建教师（用户 + 档案）
     */
    Teacher createTeacher(TeacherRegisterRequest request);

    /**
     * 创建课程
     */
    Course createCourse(AdminCreateCourseRequest request);

    /**
     * 创建教学班
     */
    TeachingClass createTeachingClass(AdminCreateTeachingClassRequest request);

    /**
     * 查询所有课程
     */
    List<Course> listCourses();

    /**
     * 查询所有教师
     */
    List<Teacher> listTeachers();

    /**
     * 查询所有学生
     */
    List<Student> listStudents();

    /**
     * 查询所有教学班
     */
    List<Map<String, Object>> listTeachingClasses();

    /**
     * 删除学生
     */
    void deleteStudent(Long studentId);

    /**
     * 删除教师
     */
    void deleteTeacher(Long teacherId);

    /**
     * 删除课程
     */
    void deleteCourse(Long courseId);
}