package com.cqu.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.Score;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩Mapper接口
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
    // 使用 MyBatis-Plus 的基础方法即可
    // selectById, selectList, selectByMap 等
}