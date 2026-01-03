package com.cqu.exp04.service;

import com.cqu.exp04.dto.AIConsultRequest;
import com.cqu.exp04.entity.Score;
import com.cqu.exp04.mapper.ScoreMapper;
import com.cqu.exp04.vo.ClassScoreStatisticsVO;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * AI服务 - 使用LangChain4j
 */
@Service
public class AIService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private StreamingChatLanguageModel streamingChatLanguageModel;

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
                你是一位资深的计算机专业学业导师,精通各类编程语言、算法、数据库等计算机课程的核心知识点。

                根据以下学生的成绩情况,回答学生的问题并提供专业建议。

                %s

                学生的问题: %s

                回答要求:
                1. **首先直接回答核心问题**: 针对学生的核心问题给出明确、直接的回答,不要绕弯子
                2. **成绩分析**: 简要分析学生的成绩情况,指出优势科目和薄弱环节
                3. **知识点诊断**: 如果学生问到具体课程(如Java、数据结构、数据库等),必须结合该课程的核心知识点进行分析
                   - 例如Java课程: 应提到面向对象编程(封装、继承、多态)、集合框架、异常处理、多线程、IO流等具体知识点
                   - 例如数据结构: 应提到线性表、栈、队列、树、图、排序算法、查找算法等
                   - 例如数据库: 应提到SQL语句、索引优化、事务处理、范式设计等
                4. **学习建议**: 针对薄弱知识点,给出具体的学习资源和练习方法
                   - 推荐教材章节、在线课程、练习项目等
                   - 给出代码练习建议和算法题推荐
                5. **实践指导**: 建议通过具体的项目实践来巩固知识

                请用自然、亲切的语气回答,避免刻板的格式,结合具体的技术术语和知识点,让建议更有针对性和可操作性。
                字数控制在400字以内。
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
                你是一位资深的计算机专业教学分析专家,精通计算机各门课程的教学重难点和知识体系。

                根据以下教学班的成绩统计数据,回答教师的问题并提供专业的教学改进建议。

                %s

                教师的问题: %s

                回答要求:
                1. **首先直接回答核心问题**: 针对教师的核心问题给出明确、直接的回答,不要绕弯子
                2. **整体教学效果评价**: 简要分析平均分、及格率、优秀率等指标
                3. **成绩分布分析**: 识别高分段、中等段、低分段学生的比例
                4. **具体知识点诊断**（重点）
                   - 根据课程名称,分析学生可能薄弱的具体知识点
                   - 例如Java课程: 如果平时分低,可能是面向对象编程(封装、继承、多态)理解不深;如果实验分低,可能是集合框架、IO流、多线程等实践能力不足;如果期末分低,可能是JVM原理、设计模式等理论掌握不够
                   - 例如数据结构: 如果成绩两极分化,可能是树、图等复杂数据结构掌握不均;如果整体偏低,可能是动态规划、贪心算法等高级算法理解困难
                   - 例如数据库: 如果实验分低,可能是SQL复杂查询(多表连接、子查询、聚合函数)不熟练;如果理论分低,可能是事务、索引、范式等原理理解不透
                5. **教学改进建议**（必须具体到知识点）
                   - **重点加强的章节**: 明确指出需要重点讲解的具体章节和知识点
                     例如: "建议在下次授课时重点加强第5章《继承与多态》中的抽象类和接口部分,增加2-3个实际案例演示"
                   - **教学方法调整**: 针对薄弱知识点,建议采用案例教学、代码演示、分组讨论等具体方法
                   - **课后辅导**: 为不及格和低分学生制定补习计划,列出具体的知识点清单
                   - **考核方式**: 建议调整平时作业、实验项目的形式,更好地检验学生对核心知识点的掌握

                请用自然、专业的语气回答,避免刻板的格式,必须结合课程的具体知识点给出建议,让回答更贴近实际教学需求。
                字数控制在500字以内。
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

    /**
     * 学生咨询 - 流式输出（同步版本）
     */
    public void studentConsultStreamingSync(Long studentId, String message, SseEmitter emitter) {
        try {
            // 获取学生成绩数据
            List<Score> scores = scoreMapper.findByStudentId(studentId);

            if (scores.isEmpty()) {
                emitter.send(SseEmitter.event().data("您还没有任何成绩记录。请先选课并等待老师录入成绩。"));
                emitter.complete();
                return;
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
                    你是一位资深的计算机专业学业导师,精通各类编程语言、算法、数据库等计算机课程的核心知识点。

                    根据以下学生的成绩情况,回答学生的问题并提供专业建议。

                    %s

                    学生的问题: %s

                    回答要求:
                    1. **首先直接回答核心问题**: 针对学生的核心问题给出明确、直接的回答,不要绕弯子
                    2. **成绩分析**: 简要分析学生的成绩情况,指出优势科目和薄弱环节
                    3. **知识点诊断**: 如果学生问到具体课程(如Java、数据结构、数据库等),必须结合该课程的核心知识点进行分析
                       - 例如Java课程: 应提到面向对象编程(封装、继承、多态)、集合框架、异常处理、多线程、IO流等具体知识点
                       - 例如数据结构: 应提到线性表、栈、队列、树、图、排序算法、查找算法等
                       - 例如数据库: 应提到SQL语句、索引优化、事务处理、范式设计等
                    4. **学习建议**: 针对薄弱知识点,给出具体的学习资源和练习方法
                       - 推荐教材章节、在线课程、练习项目等
                       - 给出代码练习建议和算法题推荐
                    5. **实践指导**: 建议通过具体的项目实践来巩固知识

                    请用自然、亲切的语气回答,避免刻板的格式,结合具体的技术术语和知识点,让建议更有针对性和可操作性。
                    字数控制在400字以内。
                    """, context.toString(), message);

            // 同步流式生成
            streamingChatLanguageModel.generate(prompt, new StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    try {
                        emitter.send(SseEmitter.event().data(token));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    emitter.complete();
                }

                @Override
                public void onError(Throwable error) {
                    emitter.completeWithError(error);
                }
            });
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    /**
     * 教师咨询 - 流式输出（同步版本）
     */
    public void teacherConsultStreamingSync(Long teacherId, Long teachingClassId, String message, SseEmitter emitter) {
        try {
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
                    你是一位资深的计算机专业教学分析专家,精通计算机各门课程的教学重难点和知识体系。

                    根据以下教学班的成绩统计数据,回答教师的问题并提供专业的教学改进建议。

                    %s

                    教师的问题: %s

                    回答要求:
                    1. **首先直接回答核心问题**: 针对教师的核心问题给出明确、直接的回答,不要绕弯子
                    2. **整体教学效果评价**: 简要分析平均分、及格率、优秀率等指标
                    3. **成绩分布分析**: 识别高分段、中等段、低分段学生的比例
                    4. **具体知识点诊断**（重点）
                       - 根据课程名称,分析学生可能薄弱的具体知识点
                       - 例如Java课程: 如果平时分低,可能是面向对象编程(封装、继承、多态)理解不深;如果实验分低,可能是集合框架、IO流、多线程等实践能力不足;如果期末分低,可能是JVM原理、设计模式等理论掌握不够
                       - 例如数据结构: 如果成绩两极分化,可能是树、图等复杂数据结构掌握不均;如果整体偏低,可能是动态规划、贪心算法等高级算法理解困难
                       - 例如数据库: 如果实验分低,可能是SQL复杂查询(多表连接、子查询、聚合函数)不熟练;如果理论分低,可能是事务、索引、范式等原理理解不透
                    5. **教学改进建议**（必须具体到知识点）
                       - **重点加强的章节**: 明确指出需要重点讲解的具体章节和知识点
                         例如: "建议在下次授课时重点加强第5章《继承与多态》中的抽象类和接口部分,增加2-3个实际案例演示"
                       - **教学方法调整**: 针对薄弱知识点,建议采用案例教学、代码演示、分组讨论等具体方法
                       - **课后辅导**: 为不及格和低分学生制定补习计划,列出具体的知识点清单
                       - **考核方式**: 建议调整平时作业、实验项目的形式,更好地检验学生对核心知识点的掌握

                    请用自然、专业的语气回答,避免刻板的格式,必须结合课程的具体知识点给出建议,让回答更贴近实际教学需求。
                    字数控制在500字以内。
                    """, context.toString(), message);

            // 同步流式生成
            streamingChatLanguageModel.generate(prompt, new StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    try {
                        emitter.send(SseEmitter.event().data(token));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    emitter.complete();
                }

                @Override
                public void onError(Throwable error) {
                    emitter.completeWithError(error);
                }
            });
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }
}