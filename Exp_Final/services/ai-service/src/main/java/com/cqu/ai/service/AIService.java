package com.cqu.ai.service;

/**
 * AI服务接口
 */
public interface AIService {

    /**
     * 学生学业咨询
     */
    String studentConsult(Long studentId, String question);

    /**
     * 教师教学咨询
     */
    String teacherConsult(Long teacherId, String question);

    /**
     * 通用AI咨询
     */
    String generalConsult(String question);
}