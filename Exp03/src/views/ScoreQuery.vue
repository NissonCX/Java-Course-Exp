<template>
  <div class="page-container">
    <h1 class="page-title">成绩查询</h1>
    
    <el-card style="margin-bottom: 24px;">
      <el-form :inline="true">
        <el-form-item label="查询方式">
          <el-radio-group v-model="queryType">
            <el-radio-button label="student">按学生查询</el-radio-button>
            <el-radio-button label="class">按教学班查询</el-radio-button>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item v-if="queryType === 'student'" label="学号/姓名">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入学号或姓名"
            clearable
            style="width: 250px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item v-else label="选择教学班">
          <el-select v-model="selectedClassId" placeholder="请选择教学班" style="width: 300px;">
            <el-option
              v-for="cls in classes"
              :key="cls.id"
              :label="`${cls.courseName} - ${cls.teacherName}`"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 学生成绩详情 -->
    <el-card v-if="queryType === 'student' && studentSummary">
      <template #header>
        <div class="card-header">
          <span>学生成绩汇总</span>
        </div>
      </template>
      
      <el-descriptions :column="3" border>
        <el-descriptions-item label="学号">{{ studentSummary.student.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ studentSummary.student.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ studentSummary.student.gender }}</el-descriptions-item>
        <el-descriptions-item label="专业" :span="2">{{ studentSummary.student.major }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ studentSummary.student.age }}</el-descriptions-item>
        <el-descriptions-item label="平均分">
          <el-tag :type="getScoreType(studentSummary.averageScore)" size="large">
            {{ studentSummary.averageScore }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总学分">{{ studentSummary.totalCredits }}</el-descriptions-item>
        <el-descriptions-item label="GPA">
          <el-tag type="success" size="large">{{ studentSummary.gpa }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      
      <el-divider />
      
      <h3 style="margin-bottom: 16px;">各科成绩详情</h3>
      <el-table :data="studentSummary.scores" stripe>
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column label="平时" width="80" align="center">
          <template #default="{ row }">
            {{ row.components.usual }}
          </template>
        </el-table-column>
        <el-table-column label="期中" width="80" align="center">
          <template #default="{ row }">
            {{ row.components.midterm }}
          </template>
        </el-table-column>
        <el-table-column label="实验" width="80" align="center">
          <template #default="{ row }">
            {{ row.components.experiment }}
          </template>
        </el-table-column>
        <el-table-column label="期末" width="80" align="center">
          <template #default="{ row }">
            {{ row.components.final }}
          </template>
        </el-table-column>
        <el-table-column label="综合成绩" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.total)" effect="dark">
              <strong>{{ row.total }}</strong>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recordTime" label="录入时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.recordTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 教学班成绩列表 -->
    <el-card v-if="queryType === 'class' && classScores.length > 0">
      <template #header>
        <div class="card-header">
          <span>{{ currentClass?.courseName }} - {{ currentClass?.teacherName }}</span>
          <el-space>
            <el-radio-group v-model="sortBy" size="small">
              <el-radio-button label="studentId">按学号排序</el-radio-button>
              <el-radio-button label="score">按成绩排序</el-radio-button>
            </el-radio-group>
          </el-space>
        </div>
      </template>
      
      <el-table :data="sortedClassScores" stripe height="500">
        <el-table-column type="index" label="排名" width="70" v-if="sortBy === 'score'" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column label="平时" width="80" align="center">
          <template #default="{ row }">
            {{ row.components.usual }}
          </template>
        </el-table-column>
        <el-table-column label="期中" width="80" align="center">
          <template #default="{ row }">
            {{ row.components.midterm }}
          </template>
        </el-table-column>
        <el-table-column label="实验" width="80" align="center">
          <template #default="{ row }">
            {{ row.components.experiment }}
          </template>
        </el-table-column>
        <el-table-column label="期末" width="80" align="center">
          <template #default="{ row }">
            {{ row.components.final }}
          </template>
        </el-table-column>
        <el-table-column label="综合成绩" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.total)" effect="dark">
              <strong>{{ row.total }}</strong>
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 16px;">
        <div style="display: flex; justify-content: space-around;">
          <el-statistic title="最高分" :value="classStats.max" />
          <el-statistic title="最低分" :value="classStats.min" />
          <el-statistic title="平均分" :value="classStats.avg" :precision="2" />
          <el-statistic title="及格率" :value="classStats.passRate" suffix="%" :precision="1" />
        </div>
      </div>
    </el-card>
    
    <el-empty v-if="queried && !studentSummary && classScores.length === 0" description="未查询到数据" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { dataService } from '@/services/dataService'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import type { StudentGradeSummary } from '@/types'
import dayjs from 'dayjs'

const queryType = ref<'student' | 'class'>('student')
const searchKeyword = ref('')
const selectedClassId = ref('')
const sortBy = ref<'studentId' | 'score'>('studentId')
const queried = ref(false)

const studentSummary = ref<StudentGradeSummary | null>(null)
const classScores = ref<any[]>([])

const classes = computed(() => dataService.getTeachingClasses())

const currentClass = computed(() => 
  classes.value.find(c => c.id === selectedClassId.value)
)

const sortedClassScores = computed(() => {
  return dataService.getClassScores(selectedClassId.value, sortBy.value)
})

const classStats = computed(() => {
  if (classScores.value.length === 0) {
    return { max: 0, min: 0, avg: 0, passRate: 0 }
  }
  
  const scores = classScores.value.map(s => s.total)
  const max = Math.max(...scores)
  const min = Math.min(...scores)
  const avg = scores.reduce((a, b) => a + b, 0) / scores.length
  const passed = scores.filter(s => s >= 60).length
  const passRate = (passed / scores.length) * 100
  
  return { max, min, avg, passRate }
})

const handleQuery = () => {
  queried.value = true
  
  if (queryType.value === 'student') {
    if (!searchKeyword.value.trim()) {
      ElMessage.warning('请输入学号或姓名')
      return
    }
    
    const result = dataService.getStudentScores(searchKeyword.value.trim())
    if (result) {
      studentSummary.value = result
      classScores.value = []
    } else {
      studentSummary.value = null
      ElMessage.error('未找到该学生')
    }
  } else {
    if (!selectedClassId.value) {
      ElMessage.warning('请选择教学班')
      return
    }
    
    classScores.value = dataService.getClassScores(selectedClassId.value, sortBy.value)
    studentSummary.value = null
    
    if (classScores.value.length === 0) {
      ElMessage.info('该教学班暂无成绩数据')
    }
  }
}

const getScoreType = (score: number) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 70) return 'warning'
  if (score >= 60) return 'info'
  return 'danger'
}

const formatDate = (date: Date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
</style>
