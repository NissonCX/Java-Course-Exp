package com.cqu.score.service.impl;

import com.cqu.common.dto.ScoreInputRequest;
import com.cqu.common.entity.Score;
import com.cqu.common.entity.ScoreWeight;
import com.cqu.score.mapper.ScoreMapper;
import com.cqu.score.mapper.ScoreWeightMapper;
import com.cqu.score.service.ScoreService;
import com.cqu.common.vo.ClassScoreStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Map<String, Object> getStudentScores(Long studentId) {
        // 获取学生所有成绩
        Map<String, Object> params = new HashMap<>();
        params.put("student_id", studentId);
        List<Score> scores = scoreMapper.selectByMap(params);

        // 计算统计信息
        Map<String, Object> result = new HashMap<>();
        result.put("scores", scores);

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

            result.put("avgScore", BigDecimal.valueOf(avgScore).setScale(2, RoundingMode.HALF_UP));
            result.put("avgGpa", BigDecimal.valueOf(avgGpa).setScale(2, RoundingMode.HALF_UP));
            result.put("totalCourses", scores.size());
            result.put("passedCourses", scores.stream()
                    .filter(s -> s.getTotalScore() != null && s.getTotalScore().compareTo(BigDecimal.valueOf(60)) >= 0)
                    .count());
        } else {
            result.put("avgScore", BigDecimal.ZERO);
            result.put("avgGpa", BigDecimal.ZERO);
            result.put("totalCourses", 0);
            result.put("passedCourses", 0);
        }

        return result;
    }

    @Override
    public List<Score> getClassScores(Long classId, Long teacherId) {
        // 简化版本：直接通过teaching_class_id查询
        Map<String, Object> params = new HashMap<>();
        params.put("teaching_class_id", classId);
        return scoreMapper.selectByMap(params);
    }

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
        // 获取权重配置
        Map<String, Object> params = new HashMap<>();
        params.put("teaching_class_id", score.getTeachingClassId());
        List<ScoreWeight> weights = scoreWeightMapper.selectByMap(params);

        ScoreWeight weight = weights.isEmpty() ? getDefaultWeight() : weights.get(0);

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