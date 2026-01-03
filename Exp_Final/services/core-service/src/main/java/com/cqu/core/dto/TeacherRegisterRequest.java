package com.cqu.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 教师注册请求DTO
 */
@Data
public class TeacherRegisterRequest {

    @NotBlank(message = "教工号不能为空")
    @Pattern(regexp = "^T\\d{4}$", message = "教工号格式错误,应为T开头后跟4位数字")
    private String teacherNo;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "性别只能是MALE或FEMALE")
    private String gender;

    private String title;

    @NotBlank(message = "院系不能为空")
    private String department;

    private String email;

    private String phone;
}
