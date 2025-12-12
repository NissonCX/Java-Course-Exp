// 学生接口
export interface Student {
  id: string
  name: string
  gender: '男' | '女'
  age: number
  major: string // 专业
}

// 教师接口
export interface Teacher {
  id: string
  name: string
  title: string // 职称
  department: string // 所属院系
}

// 课程接口
export interface Course {
  id: string
  name: string
  credits: number // 学分
  description: string
}

// 教学班接口
export interface TeachingClass {
  id: string
  courseId: string
  courseName: string
  teacherId: string
  teacherName: string
  semester: string // 学期，如 "2024-2025-1"
  totalStudents: number
  students: string[] // 学生ID列表
}

// 成绩组成部分
export interface ScoreComponent {
  usual: number // 平时成绩 (0-100)
  midterm: number // 期中考试 (0-100)
  experiment: number // 实验成绩 (0-100)
  final: number // 期末考试 (0-100)
}

// 成绩记录
export interface Score {
  id: string
  studentId: string
  studentName: string
  classId: string
  courseId: string
  courseName: string
  components: ScoreComponent
  total: number // 综合成绩
  recordTime: Date // 成绩记录时间
}

// 成绩权重配置
export interface ScoreWeight {
  usual: number // 平时成绩权重
  midterm: number // 期中成绩权重
  experiment: number // 实验成绩权重
  final: number // 期末成绩权重
}

// 默认成绩权重：平时20%，期中20%，实验20%，期末40%
export const DEFAULT_SCORE_WEIGHT: ScoreWeight = {
  usual: 0.2,
  midterm: 0.2,
  experiment: 0.2,
  final: 0.4
}

// 分数段统计
export interface ScoreDistribution {
  range: string // 如 "90-100", "80-89"
  count: number
  percentage: number
}

// 学生选课记录
export interface Enrollment {
  studentId: string
  classId: string
  courseId: string
  enrollTime: Date
}

// 学生成绩汇总
export interface StudentGradeSummary {
  student: Student
  scores: Score[]
  averageScore: number
  totalCredits: number
  gpa: number
}
