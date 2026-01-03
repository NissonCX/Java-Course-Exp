package com.cqu.exp04.service;

import com.cqu.exp04.dto.ScoreInputRequest;
import com.cqu.exp04.entity.*;
import com.cqu.exp04.mapper.*;
import com.cqu.exp04.vo.ClassScoreStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 成绩服务
 */
@Service
public class ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Autowired
    private TeachingClassMapper teachingClassMapper;

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 录入/更新成绩
     */
    @Transactional
    public void inputScore(Long teacherId, ScoreInputRequest request) {
        // 验证教师权限
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(request.getTeachingClassId())
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        if (!teachingClass.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限操作此教学班");
        }

        // 查找选课记录
        Enrollment enrollment = enrollmentMapper.findByStudentAndClass(
                request.getStudentId(), request.getTeachingClassId())
                .orElseThrow(() -> new RuntimeException("选课记录不存在"));

        // 查找或创建成绩记录
        Score score = scoreMapper.findByEnrollmentId(enrollment.getId())
                .orElseGet(() -> {
                    Score newScore = new Score();
                    newScore.setEnrollmentId(enrollment.getId());
                    newScore.setStudentId(request.getStudentId());
                    newScore.setTeachingClassId(request.getTeachingClassId());
                    return newScore;
                });

        LocalDateTime now = LocalDateTime.now();

        // 更新各项成绩
        if (request.getUsualScore() != null) {
            score.setUsualScore(request.getUsualScore());
            score.setUsualScoreTime(now);
        }
        if (request.getMidtermScore() != null) {
            score.setMidtermScore(request.getMidtermScore());
            score.setMidtermScoreTime(now);
        }
        if (request.getExperimentScore() != null) {
            score.setExperimentScore(request.getExperimentScore());
            score.setExperimentScoreTime(now);
        }
        if (request.getFinalScore() != null) {
            score.setFinalScore(request.getFinalScore());
            score.setFinalScoreTime(now);
        }

        // 如果所有成绩都有,计算总分和绩点
        if (score.getUsualScore() != null && score.getMidtermScore() != null &&
            score.getExperimentScore() != null && score.getFinalScore() != null) {

            // 使用默认权重: 平时20% + 期中20% + 实验20% + 期末40%
            BigDecimal totalScore = score.getUsualScore().multiply(new BigDecimal("0.2"))
                    .add(score.getMidtermScore().multiply(new BigDecimal("0.2")))
                    .add(score.getExperimentScore().multiply(new BigDecimal("0.2")))
                    .add(score.getFinalScore().multiply(new BigDecimal("0.4")))
                    .setScale(2, RoundingMode.HALF_UP);

            score.setTotalScore(totalScore);
            score.setGradePoint(calculateGradePoint(totalScore));
            score.setTotalScoreTime(now);
        }

        if (score.getId() == null) {
            scoreMapper.insert(score);
        } else {
            scoreMapper.updateById(score);
        }
    }

    /**
     * 计算绩点
     */
    private BigDecimal calculateGradePoint(BigDecimal totalScore) {
        double score = totalScore.doubleValue();
        if (score >= 90) return new BigDecimal("4.0");
        if (score >= 85) return new BigDecimal("3.7");
        if (score >= 82) return new BigDecimal("3.3");
        if (score >= 78) return new BigDecimal("3.0");
        if (score >= 75) return new BigDecimal("2.7");
        if (score >= 72) return new BigDecimal("2.3");
        if (score >= 68) return new BigDecimal("2.0");
        if (score >= 64) return new BigDecimal("1.5");
        if (score >= 60) return new BigDecimal("1.0");
        return new BigDecimal("0.0");
    }

    /**
     * 查询教学班成绩统计
     */
    public ClassScoreStatisticsVO getClassStatistics(Long teachingClassId) {
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(teachingClassId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        List<Score> scores = scoreMapper.findByTeachingClassId(teachingClassId);

        int totalStudents = teachingClass.getCurrentStudents();
        int scoredStudents = (int) scores.stream()
                .filter(s -> s.getTotalScore() != null)
                .count();

        double averageScore = scores.stream()
                .filter(s -> s.getTotalScore() != null)
                .mapToDouble(s -> s.getTotalScore().doubleValue())
                .average()
                .orElse(0.0);

        BigDecimal highest = scores.stream()
                .filter(s -> s.getTotalScore() != null)
                .map(Score::getTotalScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal lowest = scores.stream()
                .filter(s -> s.getTotalScore() != null)
                .map(Score::getTotalScore)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 统计分数段
        int excellent = 0, good = 0, medium = 0, pass = 0, fail = 0;
        for (Score score : scores) {
            if (score.getTotalScore() == null) continue;
            double s = score.getTotalScore().doubleValue();
            if (s >= 90) excellent++;
            else if (s >= 80) good++;
            else if (s >= 70) medium++;
            else if (s >= 60) pass++;
            else fail++;
        }

        double passRate = scoredStudents > 0 ? (double) (scoredStudents - fail) / scoredStudents * 100 : 0;
        double excellentRate = scoredStudents > 0 ? (double) excellent / scoredStudents * 100 : 0;

        return ClassScoreStatisticsVO.builder()
                .teachingClassId(teachingClassId)
                .classNo(teachingClass.getClassNo())
                .courseName(teachingClass.getCourse().getCourseName())
                .semester(teachingClass.getSemester())
                .totalStudents(totalStudents)
                .scoredStudents(scoredStudents)
                .averageScore(BigDecimal.valueOf(averageScore).setScale(2, RoundingMode.HALF_UP))
                .highestScore(highest)
                .lowestScore(lowest)
                .excellentCount(excellent)
                .goodCount(good)
                .mediumCount(medium)
                .passCount(pass)
                .failCount(fail)
                .passRate(passRate)
                .excellentRate(excellentRate)
                .build();
    }

    /**
     * 查询教学班所有学生成绩
     */
    public List<Score> getClassScores(Long teachingClassId) {
        return scoreMapper.findClassScoresWithStudent(teachingClassId);
    }

    /**
     * 查询学生所有成绩
     */
    public List<Score> getStudentScores(Long studentId) {
        return scoreMapper.findByStudentId(studentId);
    }
}
