<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="字典名称" prop="dictType">
        <el-select v-model="queryParams.dictType">
          <el-option
            v-for="item in typeOptions"
            :key="item.id"
            :label="item.dictName"
            :value="item.dictType"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="字典标签" prop="dictLabel">
        <el-input
          v-model="queryParams.dictLabel"
          placeholder="请输入字典标签"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="数据状态"
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
        <el-button type="warning" plain :icon="Close" @click="handleClose">关闭</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编码" align="center" prop="id" />
      <el-table-column label="字典标签" align="center" prop="dictLabel">
        <template #default="scope">
          <span
            v-if="
              (scope.row.listClass == '' || scope.row.listClass == 'default') &&
              (scope.row.cssClass == '' || scope.row.cssClass == null)
            "
            >{{ scope.row.dictLabel }}</span
          >
          <el-tag
            v-else
            :type="scope.row.listClass == 'primary' ? '' : scope.row.listClass"
            :class="scope.row.cssClass"
            >{{ scope.row.dictLabel }}</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column label="字典键值" align="center" prop="dictValue" />
      <el-table-column label="字典排序" align="center" prop="dictSort" />
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
        width="160"
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
      <el-form ref="dataRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典类型">
          <el-input v-model="form.dictType" :disabled="true" />
        </el-form-item>
        <el-form-item label="数据标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据键值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入数据键值" />
        </el-form-item>
        <el-form-item label="样式属性" prop="cssClass">
          <el-input v-model="form.cssClass" placeholder="请输入样式属性" />
        </el-form-item>
        <el-form-item label="显示排序" prop="dictSort">
          <el-input-number v-model="form.dictSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="回显样式" prop="listClass">
          <el-select v-model="form.listClass">
            <el-option
              v-for="item in listClassOptions"
              :key="item.value"
              :label="item.label + '(' + item.value + ')'"
              :value="item.value"
            ></el-option>
          </el-select>
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
  </div>
</template>

<script setup lang="ts" name="Data">
import { optionselect as getDictOptionselect, getType } from '@/api/system/dict/type';
import {
  listData,
  getData,
  delData,
  addData,
  updateData,
  exportData,
} from '@/api/system/dict/data';
import { formatDateTime } from '@/utils/date';
import { useDict } from '@/utils/dict';
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import useDictStore from '@/store/system/admin/dict';
import { downloadFileFromBlob } from '@/utils/download';
import { Close, Delete, Download, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue';
import type {
  SysDictData,
  SysDictType,
  DictDataQueryParams,
  DictDataSaveParams,
} from '@/types/base/api/system/dict';
import { useTabs } from '@/plugins/effects/hooks';

interface ListClassOption {
  value: string;
  label: string;
}
const router = useRouter();
const { closeCurrentTab } = useTabs();
const dataRef = ref<FormInstance>();
const queryRef = ref<FormInstance>();
const { sys_normal_disable } = useDict('sys_normal_disable');

const dataList = ref<SysDictData[]>([]);
const open = ref<boolean>(false);
const loading = ref<boolean>(true);
const showSearch = ref<boolean>(true);
const ids = ref<number[]>([]);
const single = ref<boolean>(true);
const multiple = ref<boolean>(true);
const total = ref<number>(0);
const title = ref<string>('');
const defaultDictType = ref<string>('');
const typeOptions = ref<SysDictType[]>([]);
const route = useRoute();
// 数据标签回显样式
const listClassOptions = ref<ListClassOption[]>([
  { value: 'default', label: '默认' },
  { value: 'primary', label: '主要' },
  { value: 'success', label: '成功' },
  { value: 'info', label: '信息' },
  { value: 'warning', label: '警告' },
  { value: 'danger', label: '危险' },
]);

const data = reactive({
  form: createDefaultForm(),
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    dictType: undefined,
    dictLabel: undefined,
    status: undefined,
  } as DictDataQueryParams,
  rules: {
    dictLabel: [{ required: true, message: '数据标签不能为空', trigger: 'blur' }],
    dictValue: [{ required: true, message: '数据键值不能为空', trigger: 'blur' }],
    dictSort: [{ required: true, message: '数据顺序不能为空', trigger: 'blur' }],
  },
});

const { queryParams, form, rules } = toRefs(data);

function createDefaultForm(): DictDataSaveParams {
  return {
    id: undefined,
    dictType: '',
    dictLabel: '',
    dictValue: '',
    dictSort: 0,
    cssClass: '',
    listClass: 'default',
    isDefault: '0',
    status: '1',
    remark: '',
  };
}
/** 查询字典类型详细 */
function getTypes(dictId: number) {
  getType(dictId).then((response) => {
    queryParams.value.dictType = response.dictType;
    defaultDictType.value = response.dictType!;
    getList();
  });
}

/** 查询字典类型列表 */
function getTypeList() {
  getDictOptionselect().then((response) => {
    typeOptions.value = response;
  });
}

/** 查询字典数据列表 */
function getList() {
  loading.value = true;
  listData(queryParams.value)
    .then((response) => {
      dataList.value = response.records;
      total.value = response.total;
    })
    .finally(() => {
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

  void nextTick(() => {
    dataRef.value?.clearValidate();
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 返回按钮操作 */
async function handleClose() {
  const currentTab = router.currentRoute.value;
  const failure = await router.push('/system/dict');
  if (!failure) {
    await closeCurrentTab(currentTab);
  }
}

/** 重置按钮操作 */
function resetQuery() {
  queryRef.value?.resetFields();
  queryParams.value.dictType = defaultDictType.value;
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加字典数据';
  form.value.dictType = queryParams.value.dictType ?? '';
}

/** 多选框选中数据 */
function handleSelectionChange(selection: SysDictData[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 修改按钮操作 */
async function handleUpdate(row?: SysDictData) {
  const id = row?.id ?? ids.value[0];
  if (id === undefined) {
    ElMessage.warning('请选择需要修改的数据');
    return;
  }
  const detail = await getData(id);
  form.value = {
    id: detail.id,
    dictType: detail.dictType,
    dictLabel: detail.dictLabel,
    dictValue: detail.dictValue,
    dictSort: detail.dictSort,
    cssClass: detail.cssClass ?? '',
    listClass: detail.listClass ?? 'default',
    isDefault: detail.isDefault ?? '0',
    status: detail.status,
    remark: detail.remark ?? '',
  };
  title.value = '修改字典数据';
  open.value = true;

  await nextTick();
  dataRef.value?.clearValidate();
}

/** 提交按钮 */
function submitForm() {
  dataRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != undefined) {
        updateData(form.value).then(() => {
          useDictStore().removeDict(queryParams.value.dictType!);
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addData(form.value).then(() => {
          useDictStore().removeDict(queryParams.value.dictType!);
          ElMessage.success('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: SysDictData) {
  const dictCodes = row?.id || ids.value;
  ElMessageBox.confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项？')
    .then(function () {
      return delData(dictCodes);
    })
    .then(() => {
      getList();
      ElMessage.success('删除成功');
      useDictStore().removeDict(queryParams.value.dictType!);
    })
    .catch(() => {});
}

/** 导出按钮操作 */
async function handleExport() {
  const blob = await exportData({ ...queryParams.value });
  downloadFileFromBlob({
    source: blob,
    fileName: `dict_${Date.now()}.xlsx`,
  });
}

onMounted(() => {
  getTypes(route.params && Number(route.params.dictId));
  getTypeList();
});
</script>
