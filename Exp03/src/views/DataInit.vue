<template>
  <div class="page-container">
    <h1 class="page-title">数据初始化</h1>
    
    <el-alert
      title="数据初始化说明"
      type="info"
      :closable="false"
      style="margin-bottom: 24px;"
    >
      <p>此操作将生成模拟数据，包括：</p>
      <ul style="margin: 10px 0; padding-left: 20px;">
        <li>生成 120 名学生（学号、姓名、性别、年龄、专业）</li>
        <li>生成 6 名教师（教师编号、姓名、职称、院系）</li>
        <li>生成 5 门课程（课程编号、名称、学分）</li>
        <li>自动创建教学班（每门课至少 2 个教学班，每班至少 20 人）</li>
      </ul>
      <p><strong>注意：</strong>重新初始化将清空所有现有数据！</p>
    </el-alert>
    
    <el-card>
      <el-steps :active="currentStep" finish-status="success" align-center>
        <el-step title="生成基础数据" description="教师、学生、课程" />
        <el-step title="创建教学班" description="分配教师和课程" />
        <el-step title="完成" description="数据初始化完成" />
      </el-steps>
      
      <div class="init-actions">
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          :disabled="currentStep > 0 && currentStep < 2"
          @click="initializeData"
        >
          <el-icon style="margin-right: 8px;"><Refresh /></el-icon>
          开始初始化
        </el-button>
        
        <el-button
          v-if="currentStep === 2"
          type="success"
          size="large"
          @click="$router.push('/dashboard')"
        >
          查看概览
        </el-button>
      </div>
    </el-card>
    
    <el-row :gutter="20" style="margin-top: 24px;" v-if="initialized">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><UserFilled /></el-icon>
              <span>学生列表</span>
            </div>
          </template>
          <div class="data-preview">
            <el-tag>共 {{ students.length }} 人</el-tag>
            <el-table :data="students.slice(0, 5)" height="300" size="small" stripe>
              <el-table-column prop="id" label="学号" width="100" />
              <el-table-column prop="name" label="姓名" width="80" />
              <el-table-column prop="gender" label="性别" width="60" />
              <el-table-column prop="major" label="专业" show-overflow-tooltip />
            </el-table>
            <div class="preview-note">仅显示前 5 条数据</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Avatar /></el-icon>
              <span>教师列表</span>
            </div>
          </template>
          <div class="data-preview">
            <el-tag type="success">共 {{ teachers.length }} 人</el-tag>
            <el-table :data="teachers" height="300" size="small" stripe>
              <el-table-column prop="id" label="工号" width="100" />
              <el-table-column prop="name" label="姓名" width="80" />
              <el-table-column prop="title" label="职称" width="80" />
              <el-table-column prop="department" label="院系" show-overflow-tooltip />
            </el-table>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Reading /></el-icon>
              <span>课程与教学班</span>
            </div>
          </template>
          <div class="data-preview">
            <el-space>
              <el-tag type="warning">课程 {{ courses.length }} 门</el-tag>
              <el-tag type="info">教学班 {{ classes.length }} 个</el-tag>
            </el-space>
            <el-table :data="courses" height="300" size="small" stripe>
              <el-table-column prop="id" label="编号" width="90" />
              <el-table-column prop="name" label="课程名称" show-overflow-tooltip />
              <el-table-column prop="credits" label="学分" width="60" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { dataService } from '@/services/dataService'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const currentStep = ref(0)
const initialized = ref(false)

const students = ref(dataService.getStudents())
const teachers = ref(dataService.getTeachers())
const courses = ref(dataService.getCourses())
const classes = ref(dataService.getTeachingClasses())

const initializeData = async () => {
  loading.value = true
  currentStep.value = 0
  
  try {
    // 步骤1：生成基础数据
    await new Promise(resolve => setTimeout(resolve, 500))
    currentStep.value = 1
    ElMessage.success('基础数据生成完成')
    
    // 步骤2：初始化完整数据
    await new Promise(resolve => setTimeout(resolve, 500))
    dataService.initializeData()
    currentStep.value = 2
    
    // 更新显示数据
    students.value = dataService.getStudents()
    teachers.value = dataService.getTeachers()
    courses.value = dataService.getCourses()
    classes.value = dataService.getTeachingClasses()
    
    initialized.value = true
    ElMessage.success('数据初始化完成！')
  } catch (error) {
    ElMessage.error('初始化失败，请重试')
    currentStep.value = 0
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.init-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 40px;
  padding: 20px 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.data-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-note {
  text-align: center;
  color: #909399;
  font-size: 12px;
  padding: 8px 0;
}
</style>
