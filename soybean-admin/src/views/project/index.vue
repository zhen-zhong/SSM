<template>
  <div class="h-full overflow-hidden p-4">
    <n-space vertical :size="16" class="h-full">
      <n-card :bordered="false" class="shadow-sm rounded-16px" size="small">
        <n-space justify="space-between">
          <n-space>
            <n-input v-model:value="searchName" placeholder="输入项目名称搜索" clearable @keyup.enter="handleSearch" />
            <n-button type="primary" @click="handleSearch">搜索</n-button>
          </n-space>
          <n-space>
            <n-button type="primary" @click="handleAdd">新增项目</n-button>
            <n-button @click="init">刷新</n-button>
          </n-space>
        </n-space>
      </n-card>

      <n-card :bordered="false" class="shadow-sm rounded-16px flex-1" size="small">
        <n-data-table
          remote
          :columns="columns"
          :data="tableData"
          :loading="loading"
          :pagination="pagination"
          :row-key="(row) => row.id"
        />
      </n-card>
    </n-space>

    <n-modal v-model:show="showModal" preset="card" :title="isEdit ? '编辑项目' : '新增项目'" class="w-600px">
      <!-- @vue-ignore -->
      <n-form ref="formRef" :model="formModel" :rules="rules" label-placement="left" label-width="100">
        <n-form-item label="项目名称" path="projectName">
          <n-input v-model:value="formModel.projectName" />
        </n-form-item>
        <n-form-item label="项目状态" path="status">
          <n-select v-model:value="formModel.status" :options="statusOptions" />
        </n-form-item>
        <n-form-item label="项目负责人" path="managerIds">
          <n-select v-model:value="formModel.managerIds" multiple filterable :options="userOptions" />
        </n-form-item>
        <n-form-item label="开始时间" path="startTime">
          <n-date-picker v-model:formatted-value="formModel.startTime" value-format="yyyy-MM-dd" type="date" class="w-full" />
        </n-form-item>
        <n-form-item label="预计结束" path="estimatedEndDate">
          <n-date-picker v-model:formatted-value="formModel.estimatedEndDate" value-format="yyyy-MM-dd" type="date" class="w-full" />
        </n-form-item>
        <n-form-item label="实际结束" path="actualEndDate" v-if="isEdit">
          <n-date-picker v-model:formatted-value="formModel.actualEndDate" value-format="yyyy-MM-dd" type="date" class="w-full" />
        </n-form-item>
        <n-form-item label="项目备注" path="remark">
          <n-input v-model:value="formModel.remark" type="textarea" :autosize="{ minRows: 2 }" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="tsx">
import { ref, onMounted, reactive } from 'vue';
import { NButton, NSpace, NPopconfirm, NTag, useMessage, type DataTableColumns, type FormInst } from 'naive-ui';
// 注意：这里的接口路径请根据你的实际路径进行修改
import { fetchProjectList, fetchAddProject, fetchUpdateProject, fetchDeleteProject, fetchAllUsers } from '@/service/api/system';

const message = useMessage();
const formRef = ref<FormInst | null>(null);

// --- 状态与数据控制 ---
const loading = ref(false);
const submitLoading = ref(false);
const showModal = ref(false);
const isEdit = ref(false);
const tableData = ref([]);
const searchName = ref('');

// 用户下拉框列表存放
const userOptions = ref<{ label: string; value: number }[]>([]);

// 项目状态字典
const statusOptions =[
  { label: '未开始', value: 0 },
  { label: '进行中', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已挂起', value: 3 }
];

// 状态映射字典 (用于表格渲染展示)
const statusMap: Record<number, { label: string; type: "default" | "info" | "success" | "warning" | "error" }> = {
  0: { label: '未开始', type: 'default' },
  1: { label: '进行中', type: 'info' },
  2: { label: '已完成', type: 'success' },
  3: { label: '已挂起', type: 'warning' }
};

// --- 表单模型 ---
const formModel = reactive({
  id: null as number | null,
  projectName: '',
  status: 0,
  managerIds: [] as number[],
  startTime: null as string | null,
  estimatedEndDate: null as string | null,
  actualEndDate: null as string | null,
  remark: ''
});

// --- 表单校验规则 ---
const rules = {
  projectName: { required: true, message: '请输入项目名称', trigger: 'blur' },
  status: { required: true, type: 'number', message: '请选择项目状态', trigger: 'change' },
  managerIds: { required: true, type: 'array', message: '请至少选择一位负责人', trigger: 'change' }
};

// --- 表格列定义 ---
const columns: DataTableColumns<any> = [
  { title: 'ID', key: 'id', width: 60, align: 'center' },
  { title: '项目名称', key: 'projectName', align: 'center' },
  {
    title: '状态',
    key: 'status',
    align: 'center',
    render: (row) => {
      const st = statusMap[row.status] || { label: '未知', type: 'default' };
      return <NTag type={st.type} bordered={false}>{st.label}</NTag>;
    }
  },
  { title: '负责人', key: 'managerNames', align: 'center' },
  { title: '开始时间', key: 'startTime', align: 'center' },
  { title: '预计结束', key: 'estimatedEndDate', align: 'center' },
  { title: '实际结束', key: 'actualEndDate', align: 'center' },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    align: 'center',
    render: (row) => (
      <NSpace justify="center">
        <NButton size="small" type="primary" ghost onClick={() => handleEdit(row)}>编辑</NButton>
        <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>
          {{
            trigger: () => <NButton size="small" type="error" ghost>删除</NButton>,
            default: () => '确定删除吗？'
          }}
        </NPopconfirm>
      </NSpace>
    )
  }
];

// --- 分页配置 ---
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  onChange: (page: number) => {
    pagination.page = page;
    init();
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize;
    pagination.page = 1;
    init();
  }
});

// --- 核心业务逻辑 ---

/** 加载用户字典 (用于多选下拉框) */
async function loadUsers() {
  const { data, error } = await fetchAllUsers();
  if (!error && data) {
    // 兼容取值，防止同样的嵌套问题
    const resData = data.code === 200 ? data.data : data;
    const list = Array.isArray(resData) ? resData : (resData.list || []);

    userOptions.value = list.map((user: any) => ({
      label: user.realName || user.username,
      value: user.id
    }));
  }
}

/** 触发搜索功能，将页码重置为第一页 */
function handleSearch() {
  pagination.page = 1;
  init();
}

/** 加载项目列表 */
async function init() {
  loading.value = true;
  console.log("正在请求数据...");
  try {
    const { data, error } = await fetchProjectList(pagination.page, pagination.pageSize, searchName.value);
    console.log("接口返回结果:", data);

    if (!error && data) {
      // 关键修复：兼容网络请求包装库是否帮你剥离了最外层的 {code: 200, data: {...}}
      // 如果存在 data.data 说明被套了一层，我们取内层；否则取外层。
      const responseData = data.data ? data.data : data;

      // 从内层数据中提取 list、total 等
      tableData.value = responseData.list || [];
      pagination.itemCount = responseData.total || 0;
      pagination.page = responseData.pageNum || 1;
      pagination.pageSize = responseData.pageSize || 10;

      console.log("表格数据已更新:", tableData.value);
    }
  } catch (e) {
    console.error("加载数据异常:", e);
  } finally {
    loading.value = false;
  }
}

/** 点击新增按钮 */
function handleAdd() {
  isEdit.value = false;
  Object.assign(formModel, {
    id: null,
    projectName: '',
    status: 0,
    managerIds: [],
    startTime: null,
    estimatedEndDate: null,
    actualEndDate: null,
    remark: ''
  });
  showModal.value = true;
}

/** 点击编辑按钮 */
function handleEdit(row: any) {
  isEdit.value = true;
  formModel.id = row.id;
  formModel.projectName = row.projectName;
  formModel.status = row.status;
  formModel.managerIds = row.managerIds ? [...row.managerIds] : [];
  formModel.startTime = row.startTime;
  formModel.estimatedEndDate = row.estimatedEndDate;
  formModel.actualEndDate = row.actualEndDate;
  formModel.remark = row.remark;

  showModal.value = true;
}

/** 提交保存 (新增/修改) */
async function handleSubmit() {
  await formRef.value?.validate();
  submitLoading.value = true;
  try {
    const api = isEdit.value ? fetchUpdateProject : fetchAddProject;
    const { error } = await api(formModel);

    if (!error) {
      message.success(isEdit.value ? '项目修改成功' : '项目新增成功');
      showModal.value = false;
      init();
    }
  } finally {
    submitLoading.value = false;
  }
}

/** 删除项目 */
async function handleDelete(id: number) {
  const { error } = await fetchDeleteProject(id);
  if (!error) {
    message.success('删除成功');
    init();
  }
}

// --- 初始化挂载 ---
onMounted(() => {
  loadUsers();
  init();
});
</script>

<style scoped>
.h-full { height: 100%; }
</style>
