import type {
  Student,
  Teacher,
  Course,
  TeachingClass,
  Score,
  ScoreComponent,
  Enrollment,
  ScoreDistribution,
  StudentGradeSummary
} from '@/types'
import { DEFAULT_SCORE_WEIGHT } from '@/types'

class DataService {
  private students: Student[] = []
  private teachers: Teacher[] = []
  private courses: Course[] = []
  private teachingClasses: TeachingClass[] = []
  private scores: Score[] = []
  private enrollments: Enrollment[] = []

  // 姓氏和名字库用于生成随机姓名
  private surnames = ['王', '李', '张', '刘', '陈', '杨', '黄', '赵', '周', '吴', '徐', '孙', '马', '朱', '胡', '郭', '何', '林', '高', '罗']
  private givenNames = ['伟', '芳', '娜', '敏', '静', '丽', '强', '磊', '军', '洋', '勇', '艳', '杰', '涛', '明', '超', '秀英', '娟', '婷', '雪']
  private majors = ['计算机科学与技术', '软件工程', '信息安全', '数据科学与大数据技术', '人工智能', '物联网工程']

  // 初始化所有数据
  initializeData() {
    this.generateTeachers(6)
    this.generateCourses(5)
    this.generateStudents(120)
    this.generateTeachingClasses()
    console.log('数据初始化完成')
  }

  // 生成随机教师
  private generateTeachers(count: number) {
    const titles = ['教授', '副教授', '讲师', '助教']
    const departments = ['计算机学院', '软件学院', '信息学院']
    
    this.teachers = []
    for (let i = 0; i < count; i++) {
      this.teachers.push({
        id: `T${String(i + 1).padStart(4, '0')}`,
        name: this.generateRandomName(),
        title: titles[Math.floor(Math.random() * titles.length)],
        department: departments[Math.floor(Math.random() * departments.length)]
      })
    }
  }

  // 生成随机课程
  private generateCourses(count: number) {
    const courseNames = [
      '数据结构与算法',
      'Java程序设计',
      '数据库原理',
      '计算机网络',
      '操作系统',
      'Web前端开发',
      '软件工程',
      '人工智能基础'
    ]
    
    this.courses = []
    for (let i = 0; i < count; i++) {
      this.courses.push({
        id: `C${String(i + 1).padStart(4, '0')}`,
        name: courseNames[i],
        credits: Math.floor(Math.random() * 3) + 2,
        description: `${courseNames[i]}课程描述`
      })
    }
  }

  // 生成随机学生
  private generateStudents(count: number) {
    this.students = []
    for (let i = 0; i < count; i++) {
      const gender = Math.random() > 0.5 ? '男' : '女'
      this.students.push({
        id: `S${String(i + 1).padStart(6, '0')}`,
        name: this.generateRandomName(),
        gender,
        age: Math.floor(Math.random() * 4) + 18,
        major: this.majors[Math.floor(Math.random() * this.majors.length)]
      })
    }
  }

  // 生成教学班（确保每门课至少有2个教学班，每个教学班至少20人）
  private generateTeachingClasses() {
    this.teachingClasses = []
    let classIndex = 0
    
    // 为每门课程创建至少2个教学班
    this.courses.forEach(course => {
      const classCount = 2 + Math.floor(Math.random() * 2) // 2-3个教学班
      
      for (let i = 0; i < classCount; i++) {
        const teacherIndex = classIndex % this.teachers.length
        const teacher = this.teachers[teacherIndex]
        
        this.teachingClasses.push({
          id: `CL${String(classIndex + 1).padStart(4, '0')}`,
          courseId: course.id,
          courseName: course.name,
          teacherId: teacher.id,
          teacherName: teacher.name,
          semester: '2024-2025-1',
          totalStudents: 0,
          students: []
        })
        
        classIndex++
      }
    })
  }

  // 学生选课（每个学生至少选3门课）
  enrollStudents() {
    this.enrollments = []
    
    this.students.forEach(student => {
      // 随机选择3-5门课程
      const courseCount = 3 + Math.floor(Math.random() * 3)
      const selectedCourses = this.getRandomItems(this.courses, courseCount)
      
      selectedCourses.forEach(course => {
        // 为该课程随机选择一个教学班
        const classesForCourse = this.teachingClasses.filter(c => c.courseId === course.id)
        const selectedClass = classesForCourse[Math.floor(Math.random() * classesForCourse.length)]
        
        // 添加到教学班学生列表
        if (!selectedClass.students.includes(student.id)) {
          selectedClass.students.push(student.id)
          selectedClass.totalStudents++
          
          // 记录选课信息
          this.enrollments.push({
            studentId: student.id,
            classId: selectedClass.id,
            courseId: course.id,
            enrollTime: new Date()
          })
        }
      })
    })
    
    console.log('学生选课完成')
  }

  // 生成单个教学班的某类成绩
  generateClassScores(classId: string, scoreType: keyof ScoreComponent) {
    const teachingClass = this.teachingClasses.find(c => c.id === classId)
    if (!teachingClass) return

    teachingClass.students.forEach(studentId => {
      const student = this.students.find(s => s.id === studentId)
      if (!student) return

      // 查找或创建该学生在这个教学班的成绩记录
      let scoreRecord = this.scores.find(s => s.studentId === studentId && s.classId === classId)
      
      if (!scoreRecord) {
        scoreRecord = {
          id: `SC${Date.now()}_${studentId}_${classId}`,
          studentId,
          studentName: student.name,
          classId,
          courseId: teachingClass.courseId,
          courseName: teachingClass.courseName,
          components: {
            usual: 0,
            midterm: 0,
            experiment: 0,
            final: 0
          },
          total: 0,
          recordTime: new Date()
        }
        this.scores.push(scoreRecord)
      }

      // 生成该类型成绩（正态分布，均值75，标准差10）
      scoreRecord.components[scoreType] = this.generateNormalScore(75, 10)
      scoreRecord.recordTime = new Date()
      
      // 重新计算综合成绩
      this.calculateTotalScore(scoreRecord)
    })
  }

  // 生成所有成绩
  generateAllScores() {
    this.teachingClasses.forEach(teachingClass => {
      this.generateClassScores(teachingClass.id, 'usual')
      this.generateClassScores(teachingClass.id, 'midterm')
      this.generateClassScores(teachingClass.id, 'experiment')
      this.generateClassScores(teachingClass.id, 'final')
    })
    console.log('所有成绩生成完成')
  }

  // 计算综合成绩
  private calculateTotalScore(score: Score) {
    const { usual, midterm, experiment, final } = score.components
    score.total = Math.round(
      usual * DEFAULT_SCORE_WEIGHT.usual +
      midterm * DEFAULT_SCORE_WEIGHT.midterm +
      experiment * DEFAULT_SCORE_WEIGHT.experiment +
      final * DEFAULT_SCORE_WEIGHT.final
    )
  }

  // 生成正态分布的成绩
  private generateNormalScore(mean: number, stdDev: number): number {
    // Box-Muller变换生成正态分布
    const u1 = Math.random()
    const u2 = Math.random()
    const z0 = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2)
    const score = Math.round(mean + z0 * stdDev)
    // 限制在0-100范围内
    return Math.max(0, Math.min(100, score))
  }

  // 生成随机姓名
  private generateRandomName(): string {
    const surname = this.surnames[Math.floor(Math.random() * this.surnames.length)]
    const givenName = this.givenNames[Math.floor(Math.random() * this.givenNames.length)]
    return surname + givenName
  }

  // 随机选择数组中的n个元素
  private getRandomItems<T>(array: T[], count: number): T[] {
    const shuffled = [...array].sort(() => Math.random() - 0.5)
    return shuffled.slice(0, Math.min(count, array.length))
  }

  // 获取教学班的成绩列表
  getClassScores(classId: string, sortBy: 'studentId' | 'score' = 'studentId'): Score[] {
    let classScores = this.scores.filter(s => s.classId === classId)
    
    if (sortBy === 'studentId') {
      classScores.sort((a, b) => a.studentId.localeCompare(b.studentId))
    } else {
      classScores.sort((a, b) => b.total - a.total)
    }
    
    return classScores
  }

  // 查询学生所有成绩
  getStudentScores(studentIdOrName: string): StudentGradeSummary | null {
    const student = this.students.find(
      s => s.id === studentIdOrName || s.name === studentIdOrName
    )
    
    if (!student) return null
    
    const studentScores = this.scores.filter(s => s.studentId === student.id)
    const averageScore = studentScores.length > 0
      ? studentScores.reduce((sum, s) => sum + s.total, 0) / studentScores.length
      : 0
    
    // 计算总学分
    const totalCredits = studentScores.reduce((sum, score) => {
      const course = this.courses.find(c => c.id === score.courseId)
      return sum + (course?.credits || 0)
    }, 0)
    
    // 计算GPA (简化版: 90-100=4.0, 80-89=3.0, 70-79=2.0, 60-69=1.0, <60=0)
    const gpa = studentScores.length > 0
      ? studentScores.reduce((sum, s) => {
          if (s.total >= 90) return sum + 4.0
          if (s.total >= 80) return sum + 3.0
          if (s.total >= 70) return sum + 2.0
          if (s.total >= 60) return sum + 1.0
          return sum
        }, 0) / studentScores.length
      : 0
    
    return {
      student,
      scores: studentScores,
      averageScore: Math.round(averageScore * 100) / 100,
      totalCredits,
      gpa: Math.round(gpa * 100) / 100
    }
  }

  // 统计分数段分布
  getScoreDistribution(courseId?: string): ScoreDistribution[] {
    const targetScores = courseId
      ? this.scores.filter(s => s.courseId === courseId)
      : this.scores
    
    const ranges = [
      { range: '90-100', min: 90, max: 100 },
      { range: '80-89', min: 80, max: 89 },
      { range: '70-79', min: 70, max: 79 },
      { range: '60-69', min: 60, max: 69 },
      { range: '0-59', min: 0, max: 59 }
    ]
    
    const total = targetScores.length
    
    return ranges.map(({ range, min, max }) => {
      const count = targetScores.filter(s => s.total >= min && s.total <= max).length
      return {
        range,
        count,
        percentage: total > 0 ? Math.round((count / total) * 10000) / 100 : 0
      }
    })
  }

  // 获取所有学生排名
  getAllStudentsRanking(): StudentGradeSummary[] {
    const summaries = this.students.map(student => this.getStudentScores(student.id)).filter(s => s !== null) as StudentGradeSummary[]
    return summaries.sort((a, b) => b.averageScore - a.averageScore)
  }

  // Getters
  getStudents() { return this.students }
  getTeachers() { return this.teachers }
  getCourses() { return this.courses }
  getTeachingClasses() { return this.teachingClasses }
  getAllScores() { return this.scores }
  getEnrollments() { return this.enrollments }
}

export const dataService = new DataService()
