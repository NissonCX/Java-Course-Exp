package com.cqu.exp04.service;

import com.cqu.exp04.dto.AdminCreateCourseRequest;
import com.cqu.exp04.dto.AdminCreateTeachingClassRequest;
import com.cqu.exp04.dto.StudentRegisterRequest;
import com.cqu.exp04.dto.TeacherRegisterRequest;
import com.cqu.exp04.entity.Course;
import com.cqu.exp04.entity.Student;
import com.cqu.exp04.entity.Teacher;
import com.cqu.exp04.entity.TeachingClass;

import java.util.List;

/**
 * 超级管理员服务接口
 */
public interface AdminService {

    Student createStudent(StudentRegisterRequest request);

    Teacher createTeacher(TeacherRegisterRequest request);

    Course createCourse(AdminCreateCourseRequest request);

    TeachingClass createTeachingClass(AdminCreateTeachingClassRequest request);

    List<Course> listCourses();

    List<Teacher> listTeachers();
}
