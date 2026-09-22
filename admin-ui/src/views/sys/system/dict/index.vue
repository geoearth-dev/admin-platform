<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="字典名称" prop="dictName">
        <el-input
          v-model="queryParams.dictName"
          placeholder="请输入字典名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="字典类型" prop="dictType">
        <el-input
          v-model="queryParams.dictType"
          placeholder="请输入字典类型"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="字典状态"
          clearable
        >
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" style="width: 308px">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :icon="Plus"
          @click="handleAdd"
          v-access="['system:dict:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          :icon="Edit"
          :disabled="single"
          @click="handleUpdate()"
          v-access="['system:dict:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete()"
          v-access="['system:dict:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          :icon="Download"
          @click="handleExport"
          v-access="['system:dict:export']"
          >导出</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Refresh"
          @click="handleRefreshCache"
          v-access="['system:dict:remove']"
          >刷新缓存</el-button
        >
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编号" align="center" prop="id" />
      <el-table-column
        label="字典名称"
        align="center"
        prop="dictName"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="字典类型" align="center" :show-overflow-tooltip="true">
        <template #default="scope">
          <el-button link type="primary" @click.stop="handleViewData(scope.row)">
            {{ scope.row.dictType }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        width="280"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            :icon="Edit"
            @click="handleUpdate(scope.row)"
            v-access="['system:dict:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="primary"
            :icon="Operation"
            @click="handleDataList(scope.row)"
            v-access="['system:dict:edit']"
            >列表</el-button
          >
          <el-button
            link
            type="danger"
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-access="['system:dict:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dictRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" />
          <template #label>
            <span>
              <el-tooltip content="数据存储中的Key值，如：sys_user_sex" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              字典类型
            </span>
          </template>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{
              dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <dict-data-drawer v-if="drawerRow" v-model:visible="drawerVisible" :row="drawerRow" />
  </div>
</template>

<script setup lang="ts" name="Dict">
import DictDataDrawer from './detail.vue';
import {
  listType,
  getType,
  delType,
  addType,
  updateType,
  refreshCache,
  exportType,
} from '@/api/system/dict/type';
import router from '@/router/index.ts';
import useDictStore from '@/store/system/admin/dict.ts';
import type {
  DictTypeQueryParams,
  DictTypeSaveParams,
  SysDictType,
} from '@/types/base/api/system/dict';
import { formatDateTime } from '@/utils/date.ts';
import { useDict } from '@/utils/dict';
import { downloadFileFromBlob } from '@/utils/download.ts';
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import { reactive, toRefs, ref, nextTick } from 'vue';
import { Delete, Download, Edit, Plus, Refresh, Search, Operation, QuestionFilled } from '@element-plus/icons-vue';

const queryRef = ref<FormInstance>();
const dictRef = ref<FormInstance>();

const { sys_normal_disable } = useDict('sys_normal_disable');

const typeList = ref<SysDictType[]>([]);
const open = ref<boolean>(false);
const loading = ref<boolean>(true);
const showSearch = ref<boolean>(true);
const ids = ref<number[]>([]);
const single = ref<boolean>(true);
const multiple = ref<boolean>(true);
const total = ref<number>(0);
const title = ref<string>('');
const dateRange = ref<[string, string] | null>(null);
const drawerVisible = ref<boolean>(false);
const drawerRow = ref<SysDictType | null>(null);

const data = reactive<{
  form: DictTypeSaveParams;
  queryParams: DictTypeQueryParams;
  rules: FormRules<DictTypeSaveParams>;
}>({
  form: createDefaultForm(),
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    dictName: undefined,
    dictType: undefined,
    status: undefined,
  },
  rules: {
    dictName: [
      { required: true, message: '字典名称不能为空', trigger: 'blur' },
      { max: 100, message: '字典名称不能超过100个字符', trigger: 'blur' },
    ],
    dictType: [
      { required: true, message: '字典类型不能为空', trigger: 'blur' },
      { max: 100, message: '字典类型不能超过100个字符', trigger: 'blur' },
      {
        pattern: /^[a-z][a-z0-9_]*$/,
        message: '以小写字母开头，只能包含小写字母、数字和下划线',
        trigger: 'blur',
      },
    ],
  },
});

const { queryParams, form, rules } = toRefs(data);
function createDefaultForm(): DictTypeSaveParams {
  return {
    dictName: '',
    dictType: '',
    status: '1',
    remark: '',
  };
}
function buildQuery(): DictTypeQueryParams {
  return {
    ...queryParams.value,
    beginTime: dateRange.value?.[0],
    endTime: dateRange.value?.[1],
  };
}
/** 查询字典类型列表 */
async function getList() {
  loading.value = true;
  try {
    const page = await listType(buildQuery());
    typeList.value = page.records;
    total.value = page.total;
  } catch {
    // 请求层已提示错误。
  } finally {
    loading.value = false;
  }
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = createDefaultForm();
  void nextTick(() => dictRef.value?.clearValidate());
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = null;
  queryRef.value?.resetFields();
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加字典类型';
}

/** 多选框选中数据 */
function handleSelectionChange(selection: SysDictType[]) {
  ids.value = selection.map((item) => item.id);
  single.value = ids.value.length !== 1;
  multiple.value = ids.value.length === 0;
}

/** 字典数据抽屉 */
function handleViewData(row: SysDictType) {
  drawerRow.value = row;
  drawerVisible.value = true;
}

/** 字典数据列表页面 */
function handleDataList(row: SysDictType) {
  return router.push({
    name: 'Data',
    params: {
      dictId: String(row.id),
    },
  });
}

/** 修改按钮操作 */
async function handleUpdate(row?: SysDictType) {
  const id = row?.id ?? ids.value[0];
  if (id === undefined) return;
  const detail = await getType(id);
  form.value = {
    id: detail.id,
    dictName: detail.dictName,
    dictType: detail.dictType,
    status: detail.status,
    remark: detail.remark ?? '',
  };
  title.value = '修改字典类型';
  open.value = true;

  await nextTick();
  dictRef.value?.clearValidate();
}

/** 提交按钮 */
function submitForm() {
  dictRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != undefined) {
        updateType(form.value).then(() => {
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addType(form.value).then(() => {
          ElMessage.success('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: SysDictType) {
  const deleteIds = row ? [row.id] : [...ids.value];
  if (deleteIds.length === 0) return;
  ElMessageBox.confirm('是否确认删除字典编号为"' + deleteIds + '"的数据项？')
    .then(function () {
      return delType(deleteIds);
    })
    .then(() => {
      getList();
      ElMessage.success('删除成功');
    })
    .catch(() => {});
}

/** 导出按钮操作 */
async function handleExport() {
  const blob = await exportType(buildQuery());
  downloadFileFromBlob({
    source: blob,
    fileName: `dict_${Date.now()}.xlsx`,
  });
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    ElMessage.success('刷新成功');
    useDictStore().cleanDict();
  });
}

getList();
</script>
