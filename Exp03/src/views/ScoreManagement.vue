<template>
  <div class="page-container">
    <h1 class="page-title">成绩录入</h1>

    <el-card style="margin-bottom: 24px">
      <el-form :inline="true">
        <el-form-item label="选择教学班">
          <el-select
            v-model="selectedClassId"
            placeholder="请选择教学班"
            style="width: 300px"
          >
            <el-option
              v-for="cls in classes"
              :key="cls.id"
              :label="`${cls.courseName} - ${cls.teacherName} (${cls.id})`"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="成绩类型">
          <el-select v-model="scoreType" placeholder="选择成绩类型">
            <el-option label="平时成绩" value="usual" />
            <el-option label="期中成绩" value="midterm" />
            <el-option label="实验成绩" value="experiment" />
            <el-option label="期末成绩" value="final" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :disabled="!selectedClassId || !scoreType"
            :loading="generating"
            @click="generateScores"
          >
            生成成绩
          </el-button>

          <el-button
            type="success"
            :disabled="!selectedClassId"
            :loading="generatingAll"
            @click="generateAllScores"
          >
            生成全部成绩
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="selectedClassId">
      <template #header>
        <div class="card-header">
          <span
            >{{ currentClass?.courseName }} -
            {{ currentClass?.teacherName }}</span
          >
          <el-space>
            <el-tag>学期：{{ currentClass?.semester }}</el-tag>
            <el-tag type="success"
              >学生人数：{{ currentClass?.totalStudents }}</el-tag
            >
          </el-space>
        </div>
      </template>

      <el-table :data="classScores" stripe height="500">
        <el-table-column prop="studentId" label="学号" width="120" fixed />
        <el-table-column prop="studentName" label="姓名" width="100" fixed />
        <el-table-column label="平时成绩" width="100">
          <template #default="{ row }">
            <el-tag
              v-if="row.components.usual > 0"
              :type="getScoreType(row.components.usual)"
            >
              {{ row.components.usual }}
            </el-tag>
            <span v-else class="no-score">未录入</span>
          </template>
        </el-table-column>
        <el-table-column label="期中成绩" width="100">
          <template #default="{ row }">
            <el-tag
              v-if="row.components.midterm > 0"
              :type="getScoreType(row.components.midterm)"
            >
              {{ row.components.midterm }}
            </el-tag>
            <span v-else class="no-score">未录入</span>
          </template>
        </el-table-column>
        <el-table-column label="实验成绩" width="100">
          <template #default="{ row }">
            <el-tag
              v-if="row.components.experiment > 0"
              :type="getScoreType(row.components.experiment)"
            >
              {{ row.components.experiment }}
            </el-tag>
            <span v-else class="no-score">未录入</span>
          </template>
        </el-table-column>
        <el-table-column label="期末成绩" width="100">
          <template #default="{ row }">
            <el-tag
              v-if="row.components.final > 0"
              :type="getScoreType(row.components.final)"
            >
              {{ row.components.final }}
            </el-tag>
            <span v-else class="no-score">未录入</span>
          </template>
        </el-table-column>
        <el-table-column label="综合成绩" width="100">
          <template #default="{ row }">
            <el-tag
              v-if="row.total > 0"
              :type="getScoreType(row.total)"
              effect="dark"
            >
              <strong>{{ row.total }}</strong>
            </el-tag>
            <span v-else class="no-score">未计算</span>
          </template>
        </el-table-column>
        <el-table-column prop="recordTime" label="录入时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.recordTime) }}
          </template>
        </el-table-column>
      </el-table>

      <div class="score-weights" style="margin-top: 16px">
        <el-alert type="info" :closable="false">
          <strong>成绩权重配置：</strong>
          平时成绩 20% + 期中成绩 20% + 实验成绩 20% + 期末成绩 40% = 综合成绩
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { dataService } from "@/services/dataService";
import { ElMessage } from "element-plus";
import type { ScoreComponent } from "@/types";
import dayjs from "dayjs";

const selectedClassId = ref("");
const scoreType = ref<keyof ScoreComponent>("usual");
const generating = ref(false);
const generatingAll = ref(false);
const updateTrigger = ref(0);

const classes = computed(() => dataService.getTeachingClasses());

const currentClass = computed(() =>
  classes.value.find((c) => c.id === selectedClassId.value)
);

const classScores = computed(() => {
  // Depend on updateTrigger to force refresh
  updateTrigger.value;
  if (!selectedClassId.value) return [];
  return dataService.getClassScores(selectedClassId.value, "studentId");
});

const generateScores = async () => {
  if (!selectedClassId.value || !scoreType.value) return;

  generating.value = true;
  try {
    await new Promise((resolve) => setTimeout(resolve, 500));
    dataService.generateClassScores(selectedClassId.value, scoreType.value);
    updateTrigger.value++;

    const scoreLabels: Record<keyof ScoreComponent, string> = {
      usual: "平时成绩",
      midterm: "期中成绩",
      experiment: "实验成绩",
      final: "期末成绩",
    };

    ElMessage.success(`${scoreLabels[scoreType.value]}生成完成！`);
  } catch (error) {
    ElMessage.error("成绩生成失败");
  } finally {
    generating.value = false;
  }
};

const generateAllScores = async () => {
  if (!selectedClassId.value) return;

  generatingAll.value = true;
  try {
    await new Promise((resolve) => setTimeout(resolve, 1000));
    dataService.generateClassScores(selectedClassId.value, "usual");
    dataService.generateClassScores(selectedClassId.value, "midterm");
    dataService.generateClassScores(selectedClassId.value, "experiment");
    dataService.generateClassScores(selectedClassId.value, "final");
    updateTrigger.value++;

    ElMessage.success("全部成绩生成完成！");
  } catch (error) {
    ElMessage.error("成绩生成失败");
  } finally {
    generatingAll.value = false;
  }
};

const getScoreType = (score: number) => {
  if (score >= 90) return "success";
  if (score >= 80) return "primary";
  if (score >= 70) return "warning";
  if (score >= 60) return "info";
  return "danger";
};

const formatDate = (date: Date) => {
  return dayjs(date).format("YYYY-MM-DD HH:mm:ss");
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.no-score {
  color: #c0c4cc;
  font-size: 12px;
}
</style>
