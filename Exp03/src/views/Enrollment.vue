<template>
  <div class="page-container">
    <h1 class="page-title">学生选课</h1>
    
    <el-alert
      title="选课说明"
      type="info"
      :closable="false"
      style="margin-bottom: 24px;"
    >
      <p>系统将自动为学生分配课程：</p>
      <ul style="margin: 10px 0; padding-left: 20px;">
        <li>每个学生随机选择 3-5 门课程</li>
        <li>每门课程随机分配到该课程的一个教学班</li>
        <li>确保每个教学班至少有 20 名学生</li>
      </ul>
    </el-alert>
    
    <el-card>
      <el-button
        type="primary"
        size="large"
        :loading="loading"
        :disabled="!canEnroll"
        @click="handleEnrollment"
      >
        <el-icon style="margin-right: 8px;"><DocumentAdd /></el-icon>
        开始选课
      </el-button>
      
      <el-tag v-if="enrolled" type="success" size="large" style="margin-left: 16px;">
        选课已完成
      </el-tag>
    </el-card>
    
    <el-card style="margin-top: 24px;" v-if="enrolled">
      <template #header>
        <div class="card-header">
          <span>选课统计</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <el-statistic title="参与选课学生" :value="enrollmentStats.totalStudents" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="选课记录总数" :value="enrollmentStats.totalEnrollments" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="平均选课数" :value="enrollmentStats.avgCourses" :precision="1" />
        </el-col>
      </el-row>
    </el-card>
    
    <el-card style="margin-top: 24px;" v-if="enrolled">
      <template #header>
        <div class="card-header">
          <span>教学班学生分布</span>
          <el-select v-model="selectedCourse" placeholder="选择课程" style="width: 200px;">
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </div>
      </template>
      
      <el-table :data="classDistribution" stripe>
        <el-table-column prop="id" label="教学班编号" width="140" />
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="totalStudents" label="学生人数" width="100">
          <template #default="{ row }">
            <el-tag :type="row.totalStudents >= 20 ? 'success' : 'warning'">
              {{ row.totalStudents }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewClassStudents(row)">
              查看学生名单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 学生名单对话框 -->
    <el-dialog v-model="dialogVisible" title="学生名单" width="600px">
      <el-table :data="currentClassStudents" height="400">
        <el-table-column prop="id" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="major" label="专业" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { dataService } from '@/services/dataService'
import { ElMessage } from 'element-plus'
import { DocumentAdd } from '@element-plus/icons-vue'
import type { TeachingClass, Student } from '@/types'

const loading = ref(false)
const enrolled = ref(false)
const selectedCourse = ref('')
const dialogVisible = ref(false)
const currentClassStudents = ref<Student[]>([])

const courses = computed(() => dataService.getCourses())

const canEnroll = computed(() => {
  return dataService.getStudents().length > 0 && !enrolled.value
})

const enrollmentStats = computed(() => {
  const enrollments = dataService.getEnrollments()
  const students = dataService.getStudents()
  return {
    totalStudents: students.length,
    totalEnrollments: enrollments.length,
    avgCourses: students.length > 0 ? enrollments.length / students.length : 0
  }
})

const classDistribution = computed(() => {
  const classes = dataService.getTeachingClasses()
  if (selectedCourse.value) {
    return classes.filter(c => c.courseId === selectedCourse.value)
  }
  return classes
})

const handleEnrollment = async () => {
  loading.value = true
  
  try {
    await new Promise(resolve => setTimeout(resolve, 800))
    dataService.enrollStudents()
    enrolled.value = true
    
    // 自动选择第一门课程
    if (courses.value.length > 0) {
      selectedCourse.value = courses.value[0].id
    }
    
    ElMessage.success('选课完成！')
  } catch (error) {
    ElMessage.error('选课失败，请重试')
  } finally {
    loading.value = false
  }
}

const viewClassStudents = (teachingClass: TeachingClass) => {
  const allStudents = dataService.getStudents()
  currentClassStudents.value = allStudents.filter(s => 
    teachingClass.students.includes(s.id)
  )
  dialogVisible.value = true
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
