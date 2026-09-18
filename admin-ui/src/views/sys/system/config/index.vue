<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="参数名称" prop="configName">
        <el-input
          v-model="queryParams.configName"
          placeholder="请输入参数名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input
          v-model="queryParams.configKey"
          placeholder="请输入参数键名"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="系统内置" prop="configType">
        <el-select
          v-model="queryParams.configType"
          placeholder="系统内置"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="dict in sys_yes_no"
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
          v-hasPermi="['system:config:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          :icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:config:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:config:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          :icon="Download"
          @click="handleExport"
          v-hasPermi="['system:config:export']"
          >导出</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Refresh"
          @click="handleRefreshCache"
          v-hasPermi="['system:config:remove']"
          >刷新缓存</el-button
        >
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="参数主键" align="center" prop="id" />
      <el-table-column
        label="参数名称"
        align="center"
        prop="configName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="参数键名"
        align="center"
        prop="configKey"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="参数键值"
        align="center"
        prop="configValue"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="系统内置" align="center" prop="configType">
        <template #default="scope">
          <dict-tag :options="sys_yes_no" :value="scope.row.configType" />
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
        width="150"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            :icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:config:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="danger"
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:config:remove']"
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
      <el-form ref="configRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <el-radio-group v-model="form.configType">
            <el-radio v-for="dict in sys_yes_no" :key="dict.value" :value="dict.value">{{
              dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="Config">
import {
  listConfig,
  getConfig,
  delConfig,
  addConfig,
  updateConfig,
  refreshCache,
  exportConfig,
} from '@/api/system/config';
import { useDict } from '@/utils/dict';
import { nextTick, reactive, ref, toRefs } from 'vue';
import { addDateRange } from '@/utils/form';
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import { downloadFileFromBlob } from '@/utils/download';
import type {
  ConfigQueryParams,
  ConfigSaveParams,
  SysConfig,
} from '@/types/base/api/system/config';
import { formatDateTime } from '@/utils/date';
import { Delete, Download, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue';
const queryRef = ref<FormInstance>();
const configRef = ref<FormInstance>();

const { sys_yes_no } = useDict('sys_yes_no');

const configList = ref<SysConfig[]>([]);
const open = ref<boolean>(false);
const loading = ref<boolean>(true);
const showSearch = ref<boolean>(true);
const ids = ref<number[]>([]);
const single = ref<boolean>(true);
const multiple = ref<boolean>(true);
const total = ref<number>(0);
const title = ref<string>('');
const dateRange = ref<string[]>([]);

const data = reactive<{
  form: ConfigSaveParams;
  queryParams: ConfigQueryParams;
  rules: FormRules<ConfigSaveParams>;
}>({
  form: createDefaultForm(),
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    configName: undefined,
    configKey: undefined,
    configType: undefined,
  } as ConfigQueryParams,
  rules: {
    configName: [
      { required: true, message: '参数名称不能为空', trigger: 'blur' },
      { max: 100, message: '参数名称不能超过100个字符', trigger: 'blur' },
    ],
    configKey: [
      { required: true, message: '参数键名不能为空', trigger: 'blur' },
      { max: 100, message: '参数键名不能超过100个字符', trigger: 'blur' },
    ],
    configValue: [
      { required: true, message: '参数键值不能为空', trigger: 'blur' },
      { max: 500, message: '参数键值不能超过500个字符', trigger: 'blur' },
    ],
    configType: [{ required: true, message: '请选择系统内置标识', trigger: 'change' }],
  },
});

const { queryParams, form, rules } = toRefs(data);

function createDefaultForm(): ConfigSaveParams {
  return {
    id: undefined,
    configName: '',
    configKey: '',
    configValue: '',
    configType: 'Y',
    remark: '',
  };
}

/** 查询参数列表 */
function getList() {
  loading.value = true;
  listConfig(addDateRange(queryParams.value, dateRange.value)).then((response) => {
    configList.value = response.records;
    total.value = response.total;
    loading.value = false;
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = createDefaultForm();
  void nextTick(() => configRef.value?.clearValidate());
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: SysConfig[]) {
  ids.value = selection.map((item) => item.id!);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加参数';
}

/** 修改按钮操作 */
async function handleUpdate(row: SysConfig) {
  const id = row?.id ?? ids.value[0];
  if (id === undefined) return;
  const detail = await getConfig(id);
  form.value = detail;
  open.value = true;
  title.value = '修改参数';

  await nextTick();
  configRef.value?.clearValidate();
}

/** 提交按钮 */
function submitForm() {
  configRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != undefined) {
        updateConfig(form.value).then(() => {
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addConfig(form.value).then(() => {
          ElMessage.success('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row: SysConfig) {
  const configIds = row.id || ids.value;
  ElMessageBox.confirm('是否确认删除参数编号为"' + configIds + '"的数据项？')
    .then(function () {
      return delConfig(configIds);
    })
    .then(() => {
      getList();
      ElMessage.success('删除成功');
    })
    .catch(() => {});
}

/** 导出按钮操作 */
async function handleExport() {
  const blob = await exportConfig({ ...queryParams.value });
  downloadFileFromBlob({
    source: blob,
    fileName: `post_${Date.now()}.xlsx`,
  });
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    ElMessage.success('刷新缓存成功');
  });
}

getList();
</script>
