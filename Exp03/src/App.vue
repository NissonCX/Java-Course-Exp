<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon size="32"><School /></el-icon>
        <h2>成绩管理系统</h2>
      </div>
      
      <el-menu
        :default-active="$route.path"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>系统概览</span>
        </el-menu-item>
        
        <el-menu-item index="/data-init">
          <el-icon><Setting /></el-icon>
          <span>数据初始化</span>
        </el-menu-item>
        
        <el-menu-item index="/enrollment">
          <el-icon><DocumentAdd /></el-icon>
          <span>学生选课</span>
        </el-menu-item>
        
        <el-menu-item index="/score-management">
          <el-icon><EditPen /></el-icon>
          <span>成绩录入</span>
        </el-menu-item>
        
        <el-menu-item index="/score-query">
          <el-icon><Search /></el-icon>
          <span>成绩查询</span>
        </el-menu-item>
        
        <el-menu-item index="/statistics">
          <el-icon><PieChart /></el-icon>
          <span>统计分析</span>
        </el-menu-item>
        
        <el-menu-item index="/ranking">
          <el-icon><TrendCharts /></el-icon>
          <span>学生排名</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h3>{{ currentPageTitle }}</h3>
          <div class="user-info">
            <el-icon><User /></el-icon>
            <span>管理员</span>
          </div>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const pageMap: Record<string, string> = {
  '/dashboard': '系统概览',
  '/data-init': '数据初始化',
  '/enrollment': '学生选课',
  '/score-management': '成绩录入',
  '/score-query': '成绩查询',
  '/statistics': '统计分析',
  '/ranking': '学生排名'
}

const currentPageTitle = computed(() => pageMap[route.path] || '学生成绩管理系统')
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: linear-gradient(180deg, #304156 0%, #1f2d3d 100%);
  color: #fff;
  overflow-x: hidden;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 24px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.sidebar-menu {
  border: none;
  background: transparent;
}

:deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s;
}

:deep(.el-menu-item:hover),
:deep(.el-menu-item.is-active) {
  background: rgba(64, 158, 255, 0.2) !important;
  color: #409eff;
}

.header {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.main-content {
  background: #f5f7fa;
  padding: 24px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
