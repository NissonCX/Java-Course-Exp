<template>
  <div class="page-container">
    <h1 class="page-title">统计分析</h1>
    
    <el-card style="margin-bottom: 24px;">
      <el-form :inline="true">
        <el-form-item label="选择课程">
          <el-select v-model="selectedCourse" placeholder="全部课程" clearable style="width: 250px;">
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="updateStatistics">
            <el-icon><Histogram /></el-icon>
            更新统计
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 分数段分布 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ selectedCourse ? currentCourseName : '全部课程' }} - 分数段分布</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="distribution-bars">
            <div
              v-for="item in distribution"
              :key="item.range"
              class="distribution-item"
            >
              <div class="range-label">{{ item.range }}</div>
              <div class="bar-container">
                <div
                  class="bar"
                  :class="getBarClass(item.range)"
                  :style="{ width: item.percentage + '%' }"
                >
                  <span class="bar-text">{{ item.count }} 人</span>
                </div>
              </div>
              <div class="percentage-label">{{ item.percentage }}%</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="12">
          <el-table :data="distribution" stripe>
            <el-table-column prop="range" label="分数段" width="100" />
            <el-table-column prop="count" label="人数" width="100" />
            <el-table-column label="占比" width="120">
              <template #default="{ row }">
                <el-progress
                  :percentage="row.percentage"
                  :color="getProgressColor(row.range)"
                />
              </template>
            </el-table-column>
            <el-table-column label="等级" width="100">
              <template #default="{ row }">
                <el-tag :type="getGradeType(row.range)">
                  {{ getGrade(row.range) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 统计概览 -->
    <el-row :gutter="20" style="margin-top: 24px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="总人数" :value="totalStudents">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="优秀率" :value="excellentRate" suffix="%" :precision="1">
            <template #prefix>
              <el-icon color="#67c23a"><Trophy /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="及格率" :value="passRate" suffix="%" :precision="1">
            <template #prefix>
              <el-icon color="#409eff"><Check /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="不及格率" :value="failRate" suffix="%" :precision="1">
            <template #prefix>
              <el-icon color="#f56c6c"><Close /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 成绩分布图表 -->
    <el-card style="margin-top: 24px;">
      <template #header>
        <div class="card-header">
          <span>成绩分布可视化</span>
        </div>
      </template>
      
      <div class="chart-container">
        <div class="pie-chart">
          <div class="chart-title">分数段占比</div>
          <div class="pie-slices">
            <div
              v-for="(item, index) in distribution"
              :key="item.range"
              class="pie-item"
            >
              <div class="pie-color" :style="{ background: getPieColor(index) }"></div>
              <span>{{ item.range }}: {{ item.percentage }}%</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { dataService } from '@/services/dataService'
import { Histogram } from '@element-plus/icons-vue'
import type { ScoreDistribution } from '@/types'

const selectedCourse = ref('')
const distribution = ref<ScoreDistribution[]>([])

const courses = computed(() => dataService.getCourses())

const currentCourseName = computed(() => {
  const course = courses.value.find(c => c.id === selectedCourse.value)
  return course?.name || ''
})

const totalStudents = computed(() => {
  return distribution.value.reduce((sum, item) => sum + item.count, 0)
})

const excellentRate = computed(() => {
  const excellent = distribution.value.find(d => d.range === '90-100')
  return excellent ? excellent.percentage : 0
})

const passRate = computed(() => {
  const passed = distribution.value
    .filter(d => !d.range.startsWith('0-'))
    .reduce((sum, item) => sum + item.percentage, 0)
  return passed
})

const failRate = computed(() => {
  const failed = distribution.value.find(d => d.range === '0-59')
  return failed ? failed.percentage : 0
})

const updateStatistics = () => {
  distribution.value = dataService.getScoreDistribution(selectedCourse.value || undefined)
}

const getBarClass = (range: string) => {
  if (range === '90-100') return 'bar-excellent'
  if (range === '80-89') return 'bar-good'
  if (range === '70-79') return 'bar-medium'
  if (range === '60-69') return 'bar-pass'
  return 'bar-fail'
}

const getProgressColor = (range: string) => {
  if (range === '90-100') return '#67c23a'
  if (range === '80-89') return '#409eff'
  if (range === '70-79') return '#e6a23c'
  if (range === '60-69') return '#909399'
  return '#f56c6c'
}

const getGrade = (range: string) => {
  if (range === '90-100') return '优秀'
  if (range === '80-89') return '良好'
  if (range === '70-79') return '中等'
  if (range === '60-69') return '及格'
  return '不及格'
}

const getGradeType = (range: string) => {
  if (range === '90-100') return 'success'
  if (range === '80-89') return 'primary'
  if (range === '70-79') return 'warning'
  if (range === '60-69') return 'info'
  return 'danger'
}

const getPieColor = (index: number) => {
  const colors = ['#67c23a', '#409eff', '#e6a23c', '#909399', '#f56c6c']
  return colors[index]
}

// 初始化时加载数据
updateStatistics()
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.stat-card {
  text-align: center;
}

.distribution-bars {
  padding: 20px 0;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.range-label {
  width: 80px;
  font-weight: 600;
  color: #606266;
}

.bar-container {
  flex: 1;
  height: 40px;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}

.bar {
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 12px;
  transition: width 0.3s ease;
  min-width: 60px;
}

.bar-excellent {
  background: linear-gradient(90deg, #67c23a, #85ce61);
}

.bar-good {
  background: linear-gradient(90deg, #409eff, #66b1ff);
}

.bar-medium {
  background: linear-gradient(90deg, #e6a23c, #ebb563);
}

.bar-pass {
  background: linear-gradient(90deg, #909399, #a6a9ad);
}

.bar-fail {
  background: linear-gradient(90deg, #f56c6c, #f78989);
}

.bar-text {
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.percentage-label {
  width: 60px;
  text-align: right;
  font-weight: 600;
  color: #606266;
}

.chart-container {
  padding: 20px;
}

.pie-chart {
  max-width: 500px;
  margin: 0 auto;
}

.chart-title {
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #303133;
}

.pie-slices {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pie-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 4px;
  background: #f5f7fa;
}

.pie-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}
</style>
