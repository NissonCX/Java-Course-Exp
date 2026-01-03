package com.cqu.exp04.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学生注册请求DTO
 */
@Data
public class StudentRegisterRequest {

    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^\\d{10}$", message = "学号格式错误,应为10位数字")
    private String studentNo;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "性别只能是MALE或FEMALE")
    private String gender;

    private LocalDate birthDate;

    @NotBlank(message = "专业不能为空")
    private String major;

    private String className;

    private Integer grade;

    private Integer enrollmentYear;

    private String email;

    private String phone;
}
