package com.cqu.exp04.service;

import com.cqu.exp04.dto.AIConsultRequest;
import com.cqu.exp04.entity.Score;
import com.cqu.exp04.mapper.ScoreMapper;
import com.cqu.exp04.vo.ClassScoreStatisticsVO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI服务 - 使用LangChain4j
 */
@Service
public class AIService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private ScoreService scoreService;

    /**
     * 学生咨询 - 根据成绩提供学习建议
     */
    public String studentConsult(Long studentId, String message) {
        // 获取学生所有成绩
        List<Score> scores = scoreMapper.findByStudentId(studentId);

        if (scores.isEmpty()) {
            return "您还没有任何成绩记录。请先选课并等待老师录入成绩。";
        }

        // 构建成绩上下文
        StringBuilder context = new StringBuilder();
        context.append("学生成绩情况:\n");

        double totalAverage = 0;
        int completedCourses = 0;

        for (Score score : scores) {
            context.append(String.format("- 课程: %s, ", score.getTeachingClass().getCourse().getCourseName()));

            if (score.getTotalScore() != null) {
                context.append(String.format("总分: %.2f, ", score.getTotalScore()));
                context.append(String.format("绩点: %.1f, ", score.getGradePoint()));
                context.append("成绩构成: ");
                if (score.getUsualScore() != null)
                    context.append(String.format("平时%.0f, ", score.getUsualScore()));
                if (score.getMidtermScore() != null)
                    context.append(String.format("期中%.0f, ", score.getMidtermScore()));
                if (score.getExperimentScore() != null)
                    context.append(String.format("实验%.0f, ", score.getExperimentScore()));
                if (score.getFinalScore() != null)
                    context.append(String.format("期末%.0f", score.getFinalScore()));

                totalAverage += score.getTotalScore().doubleValue();
                completedCourses++;
            } else {
                context.append("成绩尚未完整录入");
            }
            context.append("\n");
        }

        if (completedCourses > 0) {
            totalAverage /= completedCourses;
            context.append(String.format("\n平均分: %.2f\n", totalAverage));
        }

        // 构建完整提示词
        String prompt = String.format("""
                你是一位经验丰富的学业导师。根据以下学生的成绩情况,回答学生的问题并提供建议。

                %s

                学生的问题: %s

                请提供具体、实用的建议,包括:
                1. 对学生当前学业表现的分析
                2. 需要重点关注的科目
                3. 学习方法建议
                4. 未来发展方向建议

                请用简洁、鼓励的语气回答,字数控制在300字以内。
                """, context.toString(), message);

        return chatLanguageModel.generate(prompt);
    }

    /**
     * 教师咨询 - 统计分析教学班成绩
     */
    public String teacherConsult(Long teacherId, Long teachingClassId, String message) {
        // 获取教学班统计数据
        ClassScoreStatisticsVO statistics = scoreService.getClassStatistics(teachingClassId);
        List<Score> scores = scoreMapper.findByTeachingClassId(teachingClassId);

        // 构建统计上下文
        StringBuilder context = new StringBuilder();
        context.append("教学班成绩统计:\n");
        context.append(String.format("- 课程: %s\n", statistics.getCourseName()));
        context.append(String.format("- 教学班号: %s\n", statistics.getClassNo()));
        context.append(String.format("- 学期: %s\n", statistics.getSemester()));
        context.append(String.format("- 总学生数: %d, 已录入成绩: %d\n",
                statistics.getTotalStudents(), statistics.getScoredStudents()));
        context.append(String.format("- 平均分: %.2f\n", statistics.getAverageScore()));
        context.append(String.format("- 最高分: %.2f, 最低分: %.2f\n",
                statistics.getHighestScore(), statistics.getLowestScore()));
        context.append(String.format("- 分数段分布: 优秀(%d) 良好(%d) 中等(%d) 及格(%d) 不及格(%d)\n",
                statistics.getExcellentCount(), statistics.getGoodCount(),
                statistics.getMediumCount(), statistics.getPassCount(), statistics.getFailCount()));
        context.append(String.format("- 及格率: %.1f%%, 优秀率: %.1f%%\n",
                statistics.getPassRate(), statistics.getExcellentRate()));

        // 添加详细成绩列表
        context.append("\n学生成绩详情:\n");
        scores.stream()
                .filter(s -> s.getTotalScore() != null)
                .sorted((a, b) -> b.getTotalScore().compareTo(a.getTotalScore()))
                .limit(10) // 只显示前10名
                .forEach(s -> context.append(String.format("- %s: %.2f\n",
                        s.getStudent().getName(), s.getTotalScore())));

        // 构建完整提示词
        String prompt = String.format("""
                你是一位教学分析专家。根据以下教学班的成绩统计数据,回答教师的问题并提供教学建议。

                %s

                教师的问题: %s

                请提供专业的分析和建议,包括:
                1. 整体教学效果评价
                2. 成绩分布合理性分析
                3. 需要重点关注的学生群体
                4. 教学改进建议

                请用专业、客观的语气回答,字数控制在400字以内。
                """, context.toString(), message);

        return chatLanguageModel.generate(prompt);
    }

    /**
     * 通用AI咨询
     */
    public String generalConsult(String userRole, Long roleId, AIConsultRequest request) {
        if ("STUDENT".equals(userRole)) {
            return studentConsult(roleId, request.getMessage());
        } else if ("TEACHER".equals(userRole) && request.getTeachingClassId() != null) {
            return teacherConsult(roleId, request.getTeachingClassId(), request.getMessage());
        } else {
            return "无效的咨询请求";
        }
    }
}
