<template>
  <n-space vertical :size="16" class="p-4">
    <n-card :bordered="false" class="shadow-sm rounded-16px" size="small">
      <n-space justify="space-between">
        <n-space>
          <n-button type="primary" @click="handleAdd">
            <template #icon><icon-ic-round-plus /></template>
            新增角色
          </n-button>
          <n-button @click="init">
            <template #icon><icon-ic-round-refresh /></template>
            刷新数据
          </n-button>
        </n-space>
      </n-space>
    </n-card>

    <n-card :bordered="false" class="shadow-sm rounded-16px" size="small">
      <n-data-table
        remote
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
      />
    </n-card>

    <n-modal v-model:show="showModal" preset="card" :title="isEdit ? '编辑角色' : '新增角色'" class="w-500px">
      <n-form
        ref="formRef"
        :model="formModel"
        :rules="rules"
        label-placement="left"
        label-width="80"
      >
        <n-form-item label="角色名称" path="roleName">
          <n-input v-model:value="formModel.roleName" placeholder="如：VIP用户" />
        </n-form-item>

        <n-form-item label="角色编码" path="roleCode">
          <n-input v-model:value="formModel.roleCode" :disabled="isEdit" placeholder="如：vip_user" />
        </n-form-item>

        <n-form-item label="角色描述" path="description">
          <n-input
            v-model:value="formModel.description"
            type="textarea"
            placeholder="请输入角色描述内容"
            :autosize="{ minRows: 2, maxRows: 4 }"
          />
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">确认提交</n-button>
        </n-space>
      </template>
    </n-modal>
  </n-space>
</template>

<script setup lang="tsx">
import { ref, onMounted, reactive } from 'vue';
import { NButton, NSpace, NPopconfirm, useMessage, type DataTableColumns, type FormInst } from 'naive-ui';
import { fetchRoleList, fetchAddRole, fetchUpdateRole, fetchDeleteRole } from '@/service/api/system';

const message = useMessage();
const formRef = ref<FormInst | null>(null);

// --- 状态控制 ---
const loading = ref(false);
const submitLoading = ref(false);
const showModal = ref(false);
const isEdit = ref(false);
const tableData = ref([]);

// --- 表单模型 ---
const formModel = reactive({
  id: null as number | null,
  roleName: '',
  roleCode: '',
  description: ''
});

// --- 验证规则 ---
const rules = {
  roleName: { required: true, message: '请输入角色名称', trigger: 'blur' },
  roleCode: { required: true, message: '请输入角色编码', trigger: 'blur' }
};

// --- 表格列定义 ---
const columns: DataTableColumns<any> = [
  { title: 'ID', key: 'id', width: 80, align: 'center' },
  { title: '角色名称', key: 'roleName', align: 'center' },
  { title: '角色编码', key: 'roleCode', align: 'center' },
  { title: '描述', key: 'description', align: 'center', render: (row) => row.description || '--' },
  { title: '创建时间', key: 'createTime', align: 'center' },
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
            default: () => '确定删除该角色吗？删除前系统会校验是否有用户关联。'
          }}
        </NPopconfirm>
      </NSpace>
    )
  }
];

// 🌟 核心修改：将静态 pagination 改为响应式分页对象
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

// --- 核心逻辑 ---

/** 初始化加载列表 */
async function init() {
  loading.value = true;
  try {
    // 🌟 传入分页参数，并对接返回的精简分页结构
    const { data, error } = await fetchRoleList(pagination.page, pagination.pageSize);
    if (!error && data) {
      tableData.value = data.list || [];
      pagination.itemCount = data.total || 0;
      pagination.page = data.pageNum || 1;
      pagination.pageSize = data.pageSize || 10;
    }
  } finally {
    loading.value = false;
  }
}

/** 点击新增 */
function handleAdd() {
  isEdit.value = false;
  Object.assign(formModel, { id: null, roleName: '', roleCode: '', description: '' });
  showModal.value = true;
}

/** 点击编辑 */
function handleEdit(row: any) {
  isEdit.value = true;
  Object.assign(formModel, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description || ''
  });
  showModal.value = true;
}

/** 提交保存 (新增/修改) */
async function handleSubmit() {
  await formRef.value?.validate();
  submitLoading.value = true;
  try {
    const api = isEdit.value ? fetchUpdateRole : fetchAddRole;
    const { error } = await api(formModel);

    // 如果接口请求报错，保持弹窗不关闭
    if (!error) {
      message.success(isEdit.value ? '修改成功' : '新增成功');
      showModal.value = false;
      init();
    }
  } finally {
    submitLoading.value = false;
  }
}

/** 删除角色 */
async function handleDelete(id: number) {
  const { error } = await fetchDeleteRole(id);
  // 重点：如果后端抛出 RuntimeException（如有关联用户），拦截器会显示错误，并在此处拦截
  if (!error) {
    message.success('删除成功');
    init();
  }
}

onMounted(init);
</script>
