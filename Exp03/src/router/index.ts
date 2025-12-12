import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/dashboard'
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/Dashboard.vue')
    },
    {
      path: '/data-init',
      name: 'dataInit',
      component: () => import('@/views/DataInit.vue')
    },
    {
      path: '/enrollment',
      name: 'enrollment',
      component: () => import('@/views/Enrollment.vue')
    },
    {
      path: '/score-management',
      name: 'scoreManagement',
      component: () => import('@/views/ScoreManagement.vue')
    },
    {
      path: '/score-query',
      name: 'scoreQuery',
      component: () => import('@/views/ScoreQuery.vue')
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('@/views/Statistics.vue')
    },
    {
      path: '/ranking',
      name: 'ranking',
      component: () => import('@/views/Ranking.vue')
    }
  ]
})

export default router
