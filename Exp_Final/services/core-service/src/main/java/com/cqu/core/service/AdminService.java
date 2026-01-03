package com.cqu.core.service;

import com.cqu.common.dto.AdminCreateCourseRequest;
import com.cqu.common.dto.AdminCreateTeachingClassRequest;
import com.cqu.common.dto.StudentRegisterRequest;
import com.cqu.common.dto.TeacherRegisterRequest;
import com.cqu.common.entity.Course;
import com.cqu.common.entity.Student;
import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.TeachingClass;

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
