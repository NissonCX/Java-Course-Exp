package com.cqu.score.service.impl;

import com.cqu.common.dto.ScoreInputRequest;
import com.cqu.common.entity.Score;
import com.cqu.common.entity.ScoreWeight;
import com.cqu.score.client.CourseServiceClient;
import com.cqu.score.client.StudentServiceClient;
import com.cqu.score.mapper.ScoreMapper;
import com.cqu.score.mapper.ScoreWeightMapper;
import com.cqu.score.service.ScoreService;
import com.cqu.common.vo.ClassScoreStatisticsVO;
import com.cqu.common.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 成绩服务实现类
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private ScoreWeightMapper scoreWeightMapper;

    @Autowired(required = false)
    private CourseServiceClient courseServiceClient;

    @Override
    public Map<String, Object> getStudentScores(Long studentId) {
        // 获取学生所有成绩
        Map<String, Object> params = new HashMap<>();
        params.put("student_id", studentId);
        List<Score> scores = scoreMapper.selectByMap(params);

        // 获取Authorization头用于调用其他服务
        String authorization = getAuthorizationHeader();

        // 构建包含课程信息的成绩列表
        List<Map<String, Object>> enrichedScores = new ArrayList<>();
        
        // 缓存教学班信息，避免重复调用
        Map<Long, Map<String, Object>> classDetailCache = new HashMap<>();

        for (Score score : scores) {
            Map<String, Object> scoreMap = new HashMap<>();
            scoreMap.put("id", score.getId());
            scoreMap.put("studentId", score.getStudentId());
            scoreMap.put("teachingClassId", score.getTeachingClassId());
            scoreMap.put("usualScore", score.getUsualScore());
            scoreMap.put("midtermScore", score.getMidtermScore());
            scoreMap.put("experimentScore", score.getExperimentScore());
            scoreMap.put("finalScore", score.getFinalScore());
            scoreMap.put("totalScore", score.getTotalScore());
            scoreMap.put("gradePoint", score.getGradePoint());

            // 尝试获取教学班和课程信息
            if (courseServiceClient != null && authorization != null && score.getTeachingClassId() != null) {
                try {
                    Map<String, Object> classDetail = classDetailCache.get(score.getTeachingClassId());
                    if (classDetail == null) {
                        Result<Map<String, Object>> result = courseServiceClient.getClassDetail(score.getTeachingClassId(), authorization);
                        if (result.getCode() == 200 && result.getData() != null) {
                            classDetail = result.getData();
                            classDetailCache.put(score.getTeachingClassId(), classDetail);
                        }
                    }
                    
                    if (classDetail != null) {
                        scoreMap.put("courseName", classDetail.get("courseName"));
                        scoreMap.put("courseNo", classDetail.get("courseNo"));
                        scoreMap.put("credit", classDetail.get("credit"));
                        scoreMap.put("semester", classDetail.get("semester"));
                        scoreMap.put("classNo", classDetail.get("classNo"));
                        scoreMap.put("teacherName", classDetail.get("teacherName"));
                    }
                } catch (Exception e) {
                    // 调用失败时使用默认值
                    scoreMap.put("courseName", "未知课程");
                    scoreMap.put("credit", null);
                    scoreMap.put("semester", null);
                }
            }

            enrichedScores.add(scoreMap);
        }

        // 计算统计信息
        Map<String, Object> result = new HashMap<>();
        result.put("scores", enrichedScores);

        if (!scores.isEmpty()) {
            // 计算平均分
            double avgScore = scores.stream()
                    .filter(s -> s.getTotalScore() != null)
                    .mapToDouble(s -> s.getTotalScore().doubleValue())
                    .average()
                    .orElse(0.0);

            // 计算平均绩点
            double avgGpa = scores.stream()
                    .filter(s -> s.getGradePoint() != null)
                    .mapToDouble(s -> s.getGradePoint().doubleValue())
                    .average()
                    .orElse(0.0);

            // 计算已修学分（需要从课程信息中获取）
            BigDecimal totalCredits = BigDecimal.ZERO;
            int completedCourses = 0;
            for (Map<String, Object> scoreMap : enrichedScores) {
                Object totalScoreObj = scoreMap.get("totalScore");
                if (totalScoreObj != null) {
                    BigDecimal totalScoreVal = totalScoreObj instanceof BigDecimal ? 
                            (BigDecimal) totalScoreObj : new BigDecimal(totalScoreObj.toString());
                    if (totalScoreVal.compareTo(BigDecimal.valueOf(60)) >= 0) {
                        completedCourses++;
                        Object creditObj = scoreMap.get("credit");
                        if (creditObj != null) {
                            BigDecimal credit = creditObj instanceof BigDecimal ?
                                    (BigDecimal) creditObj : new BigDecimal(creditObj.toString());
                            totalCredits = totalCredits.add(credit);
                        }
                    }
                }
            }

            result.put("averageScore", BigDecimal.valueOf(avgScore).setScale(2, RoundingMode.HALF_UP));
            result.put("gpa", BigDecimal.valueOf(avgGpa).setScale(2, RoundingMode.HALF_UP));
            result.put("totalCourses", scores.size());
            result.put("completedCourses", completedCourses);
            result.put("totalCredits", totalCredits);
        } else {
            result.put("averageScore", BigDecimal.ZERO);
            result.put("gpa", BigDecimal.ZERO);
            result.put("totalCourses", 0);
            result.put("completedCourses", 0);
            result.put("totalCredits", BigDecimal.ZERO);
        }

        return result;
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

    @Override
    public List<Score> getClassScores(Long classId, Long teacherId) {
        // 简化版本：直接通过teaching_class_id查询
        Map<String, Object> params = new HashMap<>();
        params.put("teaching_class_id", classId);
        return scoreMapper.selectByMap(params);
    }

    /**
     * 获取班级学生成绩列表（包含学生信息）
     * 用于教师端成绩管理页面
     */
    public List<Map<String, Object>> getClassStudentsWithScores(Long classId, Long teacherId) {
        // 获取班级所有成绩
        Map<String, Object> params = new HashMap<>();
        params.put("teaching_class_id", classId);
        List<Score> scores = scoreMapper.selectByMap(params);

        String authorization = getAuthorizationHeader();

        // 构建包含学生信息的成绩列表
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 缓存学生信息，避免重复调用
        Map<Long, Map<String, Object>> studentCache = new HashMap<>();

        for (Score score : scores) {
            Map<String, Object> item = new HashMap<>();
            item.put("scoreId", score.getId());
            item.put("studentId", score.getStudentId());
            item.put("teachingClassId", score.getTeachingClassId());
            item.put("usualScore", score.getUsualScore());
            item.put("midtermScore", score.getMidtermScore());
            item.put("experimentScore", score.getExperimentScore());
            item.put("finalScore", score.getFinalScore());
            item.put("totalScore", score.getTotalScore());
            item.put("gradePoint", score.getGradePoint());

            // 尝试获取学生信息
            if (studentServiceClient != null && authorization != null && score.getStudentId() != null) {
                try {
                    Map<String, Object> studentInfo = studentCache.get(score.getStudentId());
                    if (studentInfo == null) {
                        Result<Map<String, Object>> studentResult = studentServiceClient.getStudentById(score.getStudentId(), authorization);
                        if (studentResult.getCode() == 200 && studentResult.getData() != null) {
                            studentInfo = studentResult.getData();
                            studentCache.put(score.getStudentId(), studentInfo);
                        }
                    }
                    
                    if (studentInfo != null) {
                        item.put("studentNo", studentInfo.get("studentNo"));
                        item.put("studentName", studentInfo.get("name"));
                        item.put("className", studentInfo.get("className"));
                        item.put("major", studentInfo.get("major"));
                    }
                } catch (Exception e) {
                    item.put("studentNo", "-");
                    item.put("studentName", "获取失败");
                }
            }

            // 如果没有获取到学生信息，使用默认值
            if (!item.containsKey("studentNo")) {
                item.put("studentNo", "-");
                item.put("studentName", "未知学生");
            }

            result.add(item);
        }

        return result;
    }

    @Autowired(required = false)
    private StudentServiceClient studentServiceClient;

    @Override
    @Transactional
    public void inputScore(Long teacherId, ScoreInputRequest request) {
        // 验证教师是否有权限操作该教学班的成绩
        // 这里简化处理，实际应该验证teacherId和teachingClassId的关联关系

        // 根据teachingClassId和studentId查找或创建成绩记录
        Map<String, Object> params = new HashMap<>();
        params.put("teaching_class_id", request.getTeachingClassId());
        params.put("student_id", request.getStudentId());
        List<Score> scores = scoreMapper.selectByMap(params);

        Score score;
        if (scores.isEmpty()) {
            // 创建新的成绩记录
            score = new Score();
            score.setTeachingClassId(request.getTeachingClassId());
            score.setStudentId(request.getStudentId());
            scoreMapper.insert(score);
        } else {
            score = scores.get(0);
        }

        // 更新各项成绩
        if (request.getUsualScore() != null) {
            score.setUsualScore(request.getUsualScore());
        }
        if (request.getMidtermScore() != null) {
            score.setMidtermScore(request.getMidtermScore());
        }
        if (request.getExperimentScore() != null) {
            score.setExperimentScore(request.getExperimentScore());
        }
        if (request.getFinalScore() != null) {
            score.setFinalScore(request.getFinalScore());
        }

        // 自动计算总分和绩点
        calculateTotalScoreAndGpa(score);

        scoreMapper.updateById(score);
    }

    @Override
    @Transactional
    public void batchInputScores(Long teacherId, Long teachingClassId, List<ScoreInputRequest> requests) {
        for (ScoreInputRequest request : requests) {
            inputScore(teacherId, request);
        }
    }

    @Override
    public ClassScoreStatisticsVO getClassStatistics(Long classId, Long teacherId) {
        List<Score> scores = getClassScores(classId, teacherId);

        ClassScoreStatisticsVO stats = new ClassScoreStatisticsVO();
        stats.setTotalStudents(scores.size());

        if (!scores.isEmpty()) {
            List<BigDecimal> totalScores = scores.stream()
                    .map(Score::getTotalScore)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!totalScores.isEmpty()) {
                // 计算平均分
                double avgScore = totalScores.stream()
                        .mapToDouble(BigDecimal::doubleValue)
                        .average()
                        .orElse(0.0);
                stats.setAverageScore(BigDecimal.valueOf(avgScore).setScale(2, RoundingMode.HALF_UP));

                // 最高分和最低分
                stats.setHighestScore(totalScores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
                stats.setLowestScore(totalScores.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));

                // 及格率
                long passedCount = totalScores.stream()
                        .filter(score -> score.compareTo(BigDecimal.valueOf(60)) >= 0)
                        .count();
                double passRate = (double) passedCount / totalScores.size() * 100;
                stats.setPassRate(passRate);

                // 优秀率 (90分以上)
                long excellentCount = totalScores.stream()
                        .filter(score -> score.compareTo(BigDecimal.valueOf(90)) >= 0)
                        .count();
                double excellentRate = (double) excellentCount / totalScores.size() * 100;
                stats.setExcellentRate(excellentRate);

                stats.setExcellentCount((int) excellentCount);
                stats.setPassCount((int) passedCount);
            } else {
                stats.setAverageScore(BigDecimal.ZERO);
                stats.setHighestScore(BigDecimal.ZERO);
                stats.setLowestScore(BigDecimal.ZERO);
                stats.setPassRate(0.0);
                stats.setExcellentRate(0.0);
                stats.setExcellentCount(0);
                stats.setPassCount(0);
            }
        }

        return stats;
    }

    @Override
    public void calculateScore(Long scoreId) {
        Score score = scoreMapper.selectById(scoreId);
        if (score == null) {
            throw new RuntimeException("成绩记录不存在");
        }

        calculateTotalScoreAndGpa(score);
        scoreMapper.updateById(score);
    }

    @Override
    @Transactional
    public void calculateClassScores(Long classId) {
        List<Score> scores = getClassScores(classId, null);
        for (Score score : scores) {
            calculateTotalScoreAndGpa(score);
            scoreMapper.updateById(score);
        }
    }

    /**
     * 计算总分和绩点
     */
    private void calculateTotalScoreAndGpa(Score score) {
        // 直接使用默认权重配置（简化处理，避免查询 score_weight 表）
        // 如果需要按课程配置权重，需要先通过 teaching_class_id 获取 course_id
        ScoreWeight weight = getDefaultWeight();

        // 计算总分
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        if (score.getUsualScore() != null && weight.getUsualWeight() != null) {
            totalScore = totalScore.add(score.getUsualScore().multiply(weight.getUsualWeight().divide(BigDecimal.valueOf(100))));
            totalWeight = totalWeight.add(weight.getUsualWeight());
        }

        if (score.getMidtermScore() != null && weight.getMidtermWeight() != null) {
            totalScore = totalScore.add(score.getMidtermScore().multiply(weight.getMidtermWeight().divide(BigDecimal.valueOf(100))));
            totalWeight = totalWeight.add(weight.getMidtermWeight());
        }

        if (score.getExperimentScore() != null && weight.getExperimentWeight() != null) {
            totalScore = totalScore.add(score.getExperimentScore().multiply(weight.getExperimentWeight().divide(BigDecimal.valueOf(100))));
            totalWeight = totalWeight.add(weight.getExperimentWeight());
        }

        if (score.getFinalScore() != null && weight.getFinalWeight() != null) {
            totalScore = totalScore.add(score.getFinalScore().multiply(weight.getFinalWeight().divide(BigDecimal.valueOf(100))));
            totalWeight = totalWeight.add(weight.getFinalWeight());
        }

        // 如果权重不满100%，按实际权重计算
        if (totalWeight.compareTo(BigDecimal.valueOf(100)) != 0 && totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            totalScore = totalScore.multiply(BigDecimal.valueOf(100)).divide(totalWeight, 2, RoundingMode.HALF_UP);
        }

        score.setTotalScore(totalScore.setScale(2, RoundingMode.HALF_UP));

        // 计算绩点 (简化版本)
        BigDecimal gradePoint = calculateGradePoint(totalScore);
        score.setGradePoint(gradePoint);
    }

    /**
     * 获取默认权重配置
     */
    private ScoreWeight getDefaultWeight() {
        ScoreWeight defaultWeight = new ScoreWeight();
        defaultWeight.setUsualWeight(BigDecimal.valueOf(20));      // 平时成绩 20%
        defaultWeight.setMidtermWeight(BigDecimal.valueOf(20));    // 期中成绩 20%
        defaultWeight.setExperimentWeight(BigDecimal.valueOf(10)); // 实验成绩 10%
        defaultWeight.setFinalWeight(BigDecimal.valueOf(50));      // 期末成绩 50%
        return defaultWeight;
    }

    /**
     * 根据总分计算绩点
     */
    private BigDecimal calculateGradePoint(BigDecimal totalScore) {
        if (totalScore == null) {
            return BigDecimal.ZERO;
        }

        double score = totalScore.doubleValue();
        if (score >= 90) return BigDecimal.valueOf(4.0);
        if (score >= 85) return BigDecimal.valueOf(3.7);
        if (score >= 82) return BigDecimal.valueOf(3.3);
        if (score >= 78) return BigDecimal.valueOf(3.0);
        if (score >= 75) return BigDecimal.valueOf(2.7);
        if (score >= 72) return BigDecimal.valueOf(2.3);
        if (score >= 68) return BigDecimal.valueOf(2.0);
        if (score >= 65) return BigDecimal.valueOf(1.7);
        if (score >= 62) return BigDecimal.valueOf(1.3);
        if (score >= 60) return BigDecimal.valueOf(1.0);
        return BigDecimal.ZERO;
    }
}