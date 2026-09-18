<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="岗位编码" prop="postCode">
        <el-input
          v-model="queryParams.postCode"
          placeholder="请输入岗位编码"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="岗位名称" prop="postName">
        <el-input
          v-model="queryParams.postName"
          placeholder="请输入岗位名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="岗位状态"
          clearable
          style="width: 200px"
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
          v-access="['system:post:add']"
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
          v-access="['system:post:edit']"
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
          v-access="['system:post:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          :icon="Download"
          @click="handleExport"
          v-access="['system:post:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="岗位编号" align="center" prop="id" />
      <el-table-column label="岗位编码" align="center" prop="postCode" />
      <el-table-column label="岗位名称" align="center" prop="postName" />
      <el-table-column label="岗位排序" align="center" prop="postSort" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.createTime) || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="180"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            :icon="Edit"
            @click="handleUpdate(scope.row)"
            v-access="['system:post:edit']"
            >修改</el-button
          >
          <el-button
            link
             type="danger"
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-access="['system:post:remove']"
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

    <!-- 添加或修改岗位对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="postRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入编码名称" />
        </el-form-item>
        <el-form-item label="岗位顺序" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{
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
          <el-button type="primary" :loading="submitting" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="Post">
import { listPost, addPost, delPost, getPost, updatePost, exportPost } from '@/api/system/post';
import type { PostQueryParams, PostSaveParams, SysPost } from '@/types/base/api/system/post';
import { useDict } from '@/utils/dict';
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import DictTag from '@/components/DictTag/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import RightToolbar from '@/components/RightToolbar/index.vue';
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue';
import { formatDateTime } from '@/utils/date';
import { downloadFileFromBlob } from '@/utils/download';
import { Delete, Download, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue';
const { sys_normal_disable } = useDict('sys_normal_disable');

const queryRef = ref<FormInstance>();
const postRef = ref<FormInstance>();

const postList = ref<SysPost[]>([]);
const open = ref<boolean>(false);
const loading = ref<boolean>(true);
const showSearch = ref<boolean>(true);
const ids = ref<number[]>([]);
const single = ref<boolean>(true);
const multiple = ref<boolean>(true);
const total = ref<number>(0);
const title = ref<string>('');

const data = reactive<{
  form: PostSaveParams;
  queryParams: PostQueryParams;
  rules: FormRules<PostSaveParams>;
}>({
  form: createDefaultForm(),
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    postCode: undefined,
    postName: undefined,
    status: undefined,
  } as PostQueryParams,
  rules: {
    postName: [
      { required: true, message: '岗位名称不能为空', trigger: 'blur' },
      { max: 50, message: '岗位名称不能超过50个字符', trigger: 'blur' },
    ],
    postCode: [
      { required: true, message: '岗位编码不能为空', trigger: 'blur' },
      { max: 64, message: '岗位编码不能超过64个字符', trigger: 'blur' },
    ],
    postSort: [{ required: true, message: '岗位顺序不能为空', trigger: 'change' }],
  },
});

const { queryParams, form, rules } = toRefs(data);
/** 定义表单实例和默认值 */
function createDefaultForm(): PostSaveParams {
  return {
    postCode: '',
    postName: '',
    postSort: 0,
    status: '1',
    remark: '',
  };
}
/** 查询岗位列表 */
async function getList() {
  loading.value = true;
  try {
    const page = await listPost({ ...queryParams.value });
    postList.value = page.records;
    total.value = page.total;
  } catch {
    // 请求层已显示错误提示。
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
  void nextTick(() => postRef.value?.clearValidate());
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: SysPost[]) {
  ids.value = selection.map((item) => item.id).filter((id): id is number => id !== undefined);
  single.value = ids.value.length !== 1;
  multiple.value = ids.value.length === 0;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加岗位';
}

/** 修改按钮操作 */
async function handleUpdate(row?: SysPost) {
  const postId = row?.id || ids.value[0];
  if (postId === undefined) return;
  const detail = await getPost(postId);
  form.value = {
    id: detail.id,
    postCode: detail.postCode ?? '',
    postName: detail.postName ?? '',
    postSort: detail.postSort ?? 0,
    status: detail.status ?? '1',
    remark: detail.remark ?? '',
  };
  title.value = '修改岗位';
  open.value = true;

  await nextTick();
  postRef.value?.clearValidate();
}
const submitting = ref(false);
/** 提交按钮 */
function submitForm() {
  submitting.value = true;
  postRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != undefined) {
        updatePost(form.value).then(() => {
          ElMessage.success('修改成功');
          open.value = false;
          submitting.value = false;
          getList();
        });
      } else {
        addPost(form.value).then(() => {
          ElMessage.success('新增成功');
          open.value = false;
          submitting.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: SysPost) {
  const postIds = row?.id !== undefined ? [row.id] : [...ids.value];
  if (postIds.length === 0) return;
  return ElMessageBox.confirm('是否确认删除岗位编号为"' + postIds + '"的数据项？', '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      delPost(postIds)
        .then(() => {
          ElMessage.success('删除成功');
          return getList();
        })
        .catch(() => {
          // 用户取消无需提示；接口异常由请求层提示。
        });
    })
    .catch(() => {});
}
/** 导出按钮操作 */
async function handleExport() {
  try {
    const blob = await exportPost({ ...queryParams.value });
    downloadFileFromBlob({
      source: blob,
      fileName: `post_${Date.now()}.xlsx`,
    });
  } catch {
    // 请求层已显示错误提示。
  } finally {
    // exporting.value = false;
  }
}
onMounted(() => {
  void getList();
});
</script>
