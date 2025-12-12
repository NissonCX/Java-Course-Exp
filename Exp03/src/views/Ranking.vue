<template>
  <div class="page-container">
    <h1 class="page-title">学生排名</h1>

    <el-card style="margin-bottom: 24px">
      <el-form :inline="true">
        <el-form-item label="排序方式">
          <el-radio-group v-model="sortType">
            <el-radio-button label="average">平均分</el-radio-button>
            <el-radio-button label="gpa">GPA</el-radio-button>
            <el-radio-button label="credits">总学分</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="显示范围">
          <el-select v-model="displayRange" style="width: 150px">
            <el-option label="全部学生" :value="0" />
            <el-option label="前10名" :value="10" />
            <el-option label="前20名" :value="20" />
            <el-option label="前50名" :value="50" />
          </el-select>
        </el-form-item>

        <el-form-item label="筛选专业">
          <el-select
            v-model="filterMajor"
            placeholder="全部专业"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="major in majors"
              :key="major"
              :label="major"
              :value="major"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生排名列表</span>
          <el-space>
            <el-tag>总人数: {{ filteredRankings.length }}</el-tag>
            <el-button type="primary" size="small" @click="exportRankings">
              <el-icon><Download /></el-icon>
              导出排名
            </el-button>
          </el-space>
        </div>
      </template>

      <el-table
        :data="displayedRankings"
        stripe
        :row-class-name="getRowClass"
        height="600"
      >
        <el-table-column type="index" label="排名" width="80" fixed>
          <template #default="{ $index }">
            <div class="rank-badge" :class="getRankClass($index)">
              <span v-if="$index < 3" class="medal">{{
                getMedal($index)
              }}</span>
              <span v-else>{{ $index + 1 }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="student.id" label="学号" width="120" fixed />
        <el-table-column prop="student.name" label="姓名" width="100" fixed />
        <el-table-column prop="student.gender" label="性别" width="70" />
        <el-table-column
          prop="student.major"
          label="专业"
          width="200"
          show-overflow-tooltip
        />

        <el-table-column label="平均分" width="120" sortable>
          <template #default="{ row }">
            <el-tag
              :type="getScoreType(row.averageScore)"
              size="large"
              effect="dark"
            >
              <strong>{{ row.averageScore }}</strong>
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="gpa" label="GPA" width="100" sortable>
          <template #default="{ row }">
            <el-tag type="success">{{ row.gpa }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="totalCredits"
          label="总学分"
          width="100"
          sortable
        />

        <el-table-column label="课程数" width="100">
          <template #default="{ row }">
            {{ row.scores.length }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              @click="viewDetail(row)"
            >
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button
              type="success"
              link
              size="small"
              @click="viewScores(row)"
            >
              <el-icon><Document /></el-icon>
              成绩单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生详情对话框 -->
    <el-dialog v-model="detailVisible" title="学生详情" width="800px">
      <template v-if="selectedStudent">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学号">{{
            selectedStudent.student.id
          }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{
            selectedStudent.student.name
          }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{
            selectedStudent.student.gender
          }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{
            selectedStudent.student.age
          }}</el-descriptions-item>
          <el-descriptions-item label="专业" :span="2">{{
            selectedStudent.student.major
          }}</el-descriptions-item>
          <el-descriptions-item label="平均分">
            <el-tag
              :type="getScoreType(selectedStudent.averageScore)"
              size="large"
            >
              {{ selectedStudent.averageScore }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="GPA">
            <el-tag type="success" size="large">{{
              selectedStudent.gpa
            }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总学分">{{
            selectedStudent.totalCredits
          }}</el-descriptions-item>
          <el-descriptions-item label="课程数">{{
            selectedStudent.scores.length
          }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h3 style="margin-bottom: 16px">各科成绩</h3>
        <el-table :data="selectedStudent.scores" max-height="300">
          <el-table-column prop="courseName" label="课程" width="150" />
          <el-table-column label="平时" width="70">
            <template #default="{ row }">{{ row.components.usual }}</template>
          </el-table-column>
          <el-table-column label="期中" width="70">
            <template #default="{ row }">{{ row.components.midterm }}</template>
          </el-table-column>
          <el-table-column label="实验" width="70">
            <template #default="{ row }">{{
              row.components.experiment
            }}</template>
          </el-table-column>
          <el-table-column label="期末" width="70">
            <template #default="{ row }">{{ row.components.final }}</template>
          </el-table-column>
          <el-table-column label="综合成绩" width="100">
            <template #default="{ row }">
              <el-tag :type="getScoreType(row.total)" effect="dark">
                {{ row.total }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>

    <!-- 成绩单对话框 -->
    <el-dialog v-model="transcriptVisible" title="学生成绩单" width="900px">
      <div v-if="selectedStudent" class="transcript">
        <div class="transcript-header">
          <h2>学生成绩单</h2>
          <div class="student-info">
            <p><strong>学号：</strong>{{ selectedStudent.student.id }}</p>
            <p><strong>姓名：</strong>{{ selectedStudent.student.name }}</p>
            <p><strong>专业：</strong>{{ selectedStudent.student.major }}</p>
          </div>
        </div>

        <el-table :data="selectedStudent.scores" border>
          <el-table-column prop="courseName" label="课程名称" width="180" />
          <el-table-column label="平时成绩" width="90" align="center">
            <template #default="{ row }">{{ row.components.usual }}</template>
          </el-table-column>
          <el-table-column label="期中成绩" width="90" align="center">
            <template #default="{ row }">{{ row.components.midterm }}</template>
          </el-table-column>
          <el-table-column label="实验成绩" width="90" align="center">
            <template #default="{ row }">{{
              row.components.experiment
            }}</template>
          </el-table-column>
          <el-table-column label="期末成绩" width="90" align="center">
            <template #default="{ row }">{{ row.components.final }}</template>
          </el-table-column>
          <el-table-column label="综合成绩" width="100" align="center">
            <template #default="{ row }">
              <strong>{{ row.total }}</strong>
            </template>
          </el-table-column>
        </el-table>

        <div class="transcript-footer">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic
                title="平均分"
                :value="selectedStudent.averageScore"
              />
            </el-col>
            <el-col :span="8">
              <el-statistic title="GPA" :value="selectedStudent.gpa" />
            </el-col>
            <el-col :span="8">
              <el-statistic
                title="总学分"
                :value="selectedStudent.totalCredits"
              />
            </el-col>
          </el-row>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { dataService } from "@/services/dataService";
import { ElMessage } from "element-plus";
import { Download } from "@element-plus/icons-vue";
import type { StudentGradeSummary } from "@/types";

const sortType = ref<"average" | "gpa" | "credits">("average");
const displayRange = ref(0);
const filterMajor = ref("");
const detailVisible = ref(false);
const transcriptVisible = ref(false);
const selectedStudent = ref<StudentGradeSummary | null>(null);

const allRankings = computed(() => {
  const rankings = dataService.getAllStudentsRanking();

  // 根据排序类型排序
  if (sortType.value === "gpa") {
    return [...rankings].sort((a, b) => b.gpa - a.gpa);
  } else if (sortType.value === "credits") {
    return [...rankings].sort((a, b) => b.totalCredits - a.totalCredits);
  }

  return rankings;
});

const majors = computed(() => {
  const majorSet = new Set(dataService.getStudents().map((s) => s.major));
  return Array.from(majorSet);
});

const filteredRankings = computed(() => {
  if (!filterMajor.value) return allRankings.value;
  return allRankings.value.filter((r) => r.student.major === filterMajor.value);
});

const displayedRankings = computed(() => {
  if (displayRange.value === 0) return filteredRankings.value;
  return filteredRankings.value.slice(0, displayRange.value);
});

const getMedal = (index: number) => {
  const medals = ["🥇", "🥈", "🥉"];
  return medals[index];
};

const getRankClass = (index: number) => {
  if (index === 0) return "rank-gold";
  if (index === 1) return "rank-silver";
  if (index === 2) return "rank-bronze";
  return "";
};

const getRowClass = ({ rowIndex }: { rowIndex: number }) => {
  if (rowIndex === 0) return "top-1";
  if (rowIndex === 1) return "top-2";
  if (rowIndex === 2) return "top-3";
  return "";
};

const getScoreType = (score: number) => {
  if (score >= 90) return "success";
  if (score >= 80) return "primary";
  if (score >= 70) return "warning";
  if (score >= 60) return "info";
  return "danger";
};

const viewDetail = (student: StudentGradeSummary) => {
  selectedStudent.value = student;
  detailVisible.value = true;
};

const viewScores = (student: StudentGradeSummary) => {
  selectedStudent.value = student;
  transcriptVisible.value = true;
};

const exportRankings = () => {
  ElMessage.success("排名数据导出成功！（演示功能）");
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-weight: 600;
  font-size: 16px;
}

.rank-badge .medal {
  font-size: 24px;
}

.rank-gold {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.4);
}

.rank-silver {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
  box-shadow: 0 2px 8px rgba(192, 192, 192, 0.4);
}

.rank-bronze {
  background: linear-gradient(135deg, #cd7f32, #e6a055);
  box-shadow: 0 2px 8px rgba(205, 127, 50, 0.4);
}

:deep(.top-1) {
  background: rgba(255, 215, 0, 0.1);
}

:deep(.top-2) {
  background: rgba(192, 192, 192, 0.1);
}

:deep(.top-3) {
  background: rgba(205, 127, 50, 0.1);
}

.transcript {
  padding: 20px;
}

.transcript-header {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #409eff;
}

.transcript-header h2 {
  margin-bottom: 16px;
  color: #303133;
}

.student-info {
  display: flex;
  justify-content: center;
  gap: 32px;
  font-size: 14px;
}

.student-info p {
  margin: 0;
}

.transcript-footer {
  margin-top: 24px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>
