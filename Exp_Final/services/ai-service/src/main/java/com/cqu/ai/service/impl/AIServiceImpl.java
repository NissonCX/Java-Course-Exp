package com.cqu.ai.service.impl;

import com.cqu.ai.client.ScoreServiceClient;
import com.cqu.ai.client.StudentServiceClient;
import com.cqu.ai.service.AIService;
import com.cqu.common.vo.Result;
import dev.langchain4j.model.dashscope.QwenChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * AI服务实现类
 */
@Service
public class AIServiceImpl implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);

    @Autowired
    private QwenChatModel qwenChatModel;

    @Autowired
    private StudentServiceClient studentServiceClient;

    @Autowired
    private ScoreServiceClient scoreServiceClient;

    @Override
    public String studentConsult(Long studentId, String question) {
        logger.info("Student {} asking question: {}", studentId, question);

        try {
            // 获取学生信息和成绩数据用于智能分析
            String studentContext = getStudentContext(studentId);

            // 构建学生专用的提示词，包含个人信息和成绩
            String prompt = buildStudentPrompt(question, studentContext);

            // 调用AI模型
            String response = qwenChatModel.generate(prompt);

            logger.info("AI response for student {}: {}", studentId, response);
            return response;
        } catch (Exception e) {
            logger.error("Error in student consultation for student {}: ", studentId, e);
            return "抱歉，AI咨询服务暂时不可用。请稍后再试。如果问题持续存在，请联系技术支持。";
        }
    }

    @Override
    public String teacherConsult(Long teacherId, String question) {
        logger.info("Teacher {} asking question: {}", teacherId, question);

        try {
            // 构建教师专用的提示词
            String prompt = buildTeacherPrompt(question);

            // 调用AI模型
            String response = qwenChatModel.generate(prompt);

            logger.info("AI response for teacher {}: {}", teacherId, response);
            return response;
        } catch (Exception e) {
            logger.error("Error in teacher consultation for teacher {}: ", teacherId, e);
            return "抱歉，AI咨询服务暂时不可用。请稍后再试。如果问题持续存在，请联系技术支持。";
        }
    }

    @Override
    public String generalConsult(String question) {
        logger.info("General consultation question: {}", question);

        try {
            // 构建通用的提示词
            String prompt = buildGeneralPrompt(question);

            // 调用AI模型
            String response = qwenChatModel.generate(prompt);

            logger.info("AI general response: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("Error in general consultation: ", e);
            return "抱歉，AI咨询服务暂时不可用。请稍后再试。如果问题持续存在，请联系技术支持。";
        }
    }

    /**
     * 获取学生上下文信息（个人信息和成绩）
     */
    private String getStudentContext(Long studentId) {
        StringBuilder context = new StringBuilder();
        String authorization = getAuthorizationHeader();

        if (authorization != null) {
            try {
                // 获取学生基本信息
                Result<Map<String, Object>> studentResult = studentServiceClient.getStudentProfile(authorization);
                if (studentResult.getCode() == 200) {
                    Map<String, Object> student = studentResult.getData();
                    context.append("学生信息：");
                    context.append("姓名：").append(student.get("name")).append("，");
                    context.append("专业：").append(student.get("major")).append("，");
                    context.append("年级：").append(student.get("grade")).append("；");
                }

                // 获取学生成绩信息
                Result<Map<String, Object>> scoresResult = scoreServiceClient.getStudentScores(studentId, authorization);
                if (scoresResult.getCode() == 200) {
                    Map<String, Object> scores = scoresResult.getData();
                    context.append("成绩情况：");
                    if (scores.get("statistics") != null) {
                        context.append("统计信息 - ").append(scores.get("statistics")).append("；");
                    }
                    if (scores.get("scores") != null) {
                        context.append("详细成绩 - ").append(scores.get("scores"));
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to get student context for AI analysis: {}", e.getMessage());
                context.append("无法获取详细的学生信息，将基于一般情况提供建议。");
            }
        } else {
            context.append("无法获取学生详细信息，将基于一般情况提供建议。");
        }

        return context.toString();
    }

    /**
     * 构建学生专用提示词（包含个人信息和成绩）
     */
    private String buildStudentPrompt(String question, String studentContext) {
        return "你是一位经验丰富的学业指导老师，专门帮助大学生解决学习和成绩相关的问题。" +
               "以下是学生的基本信息和成绩情况：\n\n" +
               studentContext + "\n\n" +
               "请针对以下学生的问题，结合他/她的具体情况，提供专业、个性化的建议：\n\n" +
               "学生问题：" + question + "\n\n" +
               "请给出具体的学习建议、方法和步骤，语言要友好、鼓励性，并针对学生的实际情况提供个性化指导。";
    }

    /**
     * 构建教师专用提示词
     */
    private String buildTeacherPrompt(String question) {
        return "你是一位教育专家，专门为高等教育教师提供教学方法和学生管理的咨询。" +
               "请针对以下教师的问题，提供专业的教学建议：\n\n" +
               "教师问题：" + question + "\n\n" +
               "请从教学方法、学生评价、课程设计等角度提供实用的建议。";
    }

    /**
     * 构建通用提示词
     */
    private String buildGeneralPrompt(String question) {
        return "你是一位知识渊博的助手，请针对以下问题提供准确、有用的回答：\n\n" +
               "问题：" + question + "\n\n" +
               "请提供清晰、简洁的回答。";
    }

    /**
     * 从当前请求中获取 Authorization 头
     */
    private String getAuthorizationHeader() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("Authorization");
            }
        } catch (Exception e) {
            // 忽略异常，返回null
        }
        return null;
    }
}