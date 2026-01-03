package com.cqu.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.ScoreWeight;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩权重Mapper接口
 */
@Mapper
public interface ScoreWeightMapper extends BaseMapper<ScoreWeight> {
    // 使用 MyBatis-Plus 的基础方法即可
    // selectById, selectList, selectByMap 等
}