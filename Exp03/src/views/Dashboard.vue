<template>
  <div class="page-container">
    <h1 class="page-title">系统概览</h1>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon students">
            <el-icon size="40"><UserFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.studentCount }}</div>
            <div class="stat-label">学生总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon teachers">
            <el-icon size="40"><Avatar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.teacherCount }}</div>
            <div class="stat-label">教师总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon courses">
            <el-icon size="40"><Reading /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.courseCount }}</div>
            <div class="stat-label">课程总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon classes">
            <el-icon size="40"><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.classCount }}</div>
            <div class="stat-label">教学班数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>课程列表</span>
            </div>
          </template>
          <el-table :data="courses" height="300" stripe>
            <el-table-column prop="id" label="课程编号" width="120" />
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="credits" label="学分" width="80" />
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>教学班列表</span>
            </div>
          </template>
          <el-table :data="classes" height="300" stripe>
            <el-table-column prop="id" label="班级编号" width="120" />
            <el-table-column prop="courseName" label="课程" />
            <el-table-column prop="teacherName" label="教师" />
            <el-table-column prop="totalStudents" label="人数" width="80" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
        </div>
      </template>
      <div class="quick-actions">
        <el-button type="primary" :icon="Setting" @click="$router.push('/data-init')">
          数据初始化
        </el-button>
        <el-button type="success" :icon="DocumentAdd" @click="$router.push('/enrollment')">
          学生选课
        </el-button>
        <el-button type="warning" :icon="EditPen" @click="$router.push('/score-management')">
          录入成绩
        </el-button>
        <el-button type="info" :icon="Search" @click="$router.push('/score-query')">
          查询成绩
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { dataService } from '@/services/dataService'
import { ElMessage } from 'element-plus'
import { Setting, DocumentAdd, EditPen, Search } from '@element-plus/icons-vue'

const courses = ref(dataService.getCourses())
const classes = ref(dataService.getTeachingClasses())

const stats = computed(() => ({
  studentCount: dataService.getStudents().length,
  teacherCount: dataService.getTeachers().length,
  courseCount: dataService.getCourses().length,
  classCount: dataService.getTeachingClasses().length
}))

onMounted(() => {
  // 如果没有数据，显示提示
  if (stats.value.studentCount === 0) {
    ElMessage.info('请先进行数据初始化')
  }
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon.students {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-icon.teachers {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-icon.courses {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-icon.classes {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.quick-actions {
  display: flex;
  gap: 16px;
}
</style>
