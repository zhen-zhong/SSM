<template>
  <n-space vertical :size="16" class="p-4">
    <n-card :bordered="false" class="shadow-sm rounded-16px" size="small">
      <n-space justify="space-between">
        <n-space>
          <n-button type="primary" @click="handleAdd">
            <template #icon><icon-ic-round-plus /></template>
            新增用户
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
        :scroll-x="1200"
      />
    </n-card>

    <n-modal v-model:show="showModal" preset="card" :title="isEdit ? '编辑用户' : '新增用户'" class="w-550px">
      <n-form ref="formRef" :model="formModel" :rules="rules" label-placement="left" label-width="80">
        <n-form-item label="登录账号" path="userName">
          <n-input v-model:value="formModel.userName" :disabled="isEdit" placeholder="请输入登录账号" />
        </n-form-item>
        <n-form-item label="真实姓名" path="realName">
          <n-input v-model:value="formModel.realName" placeholder="请输入真实姓名" />
        </n-form-item>
        <n-form-item label="手机号码" path="phone">
          <n-input v-model:value="formModel.phone" placeholder="请输入手机号码" />
        </n-form-item>
        <n-form-item :label="isEdit ? '重置密码' : '登录密码'" :path="isEdit ? '' : 'password'">
          <n-input v-model:value="formModel.password" type="password" show-password-on="click" :placeholder="isEdit ? '若不修改请留空' : '请输入初始密码'" />
        </n-form-item>
        <n-form-item label="分配角色" path="roleIds">
          <n-select v-model:value="formModel.roleIds" multiple :options="roleOptions" placeholder="请选择角色" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSave">确认保存</n-button>
        </n-space>
      </template>
    </n-modal>
  </n-space>
</template>

<script setup lang="tsx">
import { ref, onMounted, reactive } from 'vue';
import { NButton, NSpace, NTag, useMessage, type DataTableColumns, type FormInst } from 'naive-ui';
import {
  fetchUserList,
  fetchAddUser,
  fetchUpdateUser,
  fetchDeleteUser,
  fetchAssignRole,
  fetchResetPasswordAdmin,
  fetchAllRoles
} from '@/service/api/system';

const message = useMessage();
const formRef = ref<FormInst | null>(null);
const loading = ref(false);
const submitLoading = ref(false);
const showModal = ref(false);
const isEdit = ref(false);
const tableData = ref([]);
const roleOptions = ref([]);

// 表单数据模型
const formModel = reactive({
  id: null as number | null,
  userName: '',
  realName: '',
  phone: '',
  password: '',
  roleIds:[] as number[]
});

// 校验规则
const rules = {
  userName: { required: true, message: '请输入用户名', trigger: 'blur' },
  realName: { required: true, message: '请输入真实姓名', trigger: 'blur' },
  password: { required: true, message: '请输入初始密码', trigger: 'blur' }
};

// 表格列配置
const columns: DataTableColumns<any> =[
  { title: 'ID', key: 'id', width: 60, align: 'center' },
  { title: '登录账号', key: 'userName', width: 100, align: 'center' },
  { title: '真实姓名', key: 'realName', width: 100, align: 'center' },
  {
    title: '角色',
    key: 'roles',
    width: 150,
    render: (row) => (
      <NSpace size={[4, 4]}>
        {row.roles?.map((role: any) => (
          <NTag type="primary" size="small" bordered={false} key={role.id}>{role.roleName}</NTag>
        ))}
      </NSpace>
    )
  },
  {
    title: '状态',
    key: 'status',
    width: 80,
    align: 'center',
    render: (row) => <NTag type={row.status === 1 ? 'success' : 'error'}>{row.status === 1 ? '正常' : '停用'}</NTag>
  },
  { title: '创建时间', key: 'createTime', width: 160, align: 'center' },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    align: 'center',
    fixed: 'right',
    render: (row) => (
      <NSpace justify="center">
        <NButton size="small" ghost onClick={() => handleEdit(row)}>编辑</NButton>
        <NButton size="small" type="error" ghost onClick={() => handleDelete(row.id)}>删除</NButton>
      </NSpace>
    )
  }
];

// 分页配置
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes:[10, 20, 50],
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

// 初始化数据
async function init() {
  loading.value = true;
  const { data, error } = await fetchUserList(pagination.page, pagination.pageSize);
  if (!error && data) {
    // 已经与后端处理后的精简 4 字段 Map 结构对接
    tableData.value = data.list ||[];
    pagination.itemCount = data.total || 0;
    pagination.page = data.pageNum || 1;
    pagination.pageSize = data.pageSize || 10;
  }
  loading.value = false;
}

// 保存提交
async function handleSave() {
  await formRef.value?.validate();
  submitLoading.value = true;
  try {
    const { data, error } = isEdit.value ? await fetchUpdateUser(formModel) : await fetchAddUser(formModel);

    // 如果业务 code 不为 200，拦截器已报错，此处直接返回保持弹窗
    if (error) return;

    let currentUserId = formModel.id;
    // 新增成功后获取返回的 ID
    if (!isEdit.value && data && typeof data === 'object') {
      currentUserId = data.id;
    }

    // 处理编辑模式下的密码重置
    if (isEdit.value && formModel.password && currentUserId) {
      await fetchResetPasswordAdmin(currentUserId, formModel.password);
    }

    // 处理角色分配
    if (currentUserId && formModel.roleIds.length > 0) {
      await fetchAssignRole(currentUserId, formModel.roleIds);
    }

    // 成功提示并关闭
    message.success(typeof data === 'string' ? data : '操作成功');
    showModal.value = false;
    init();
  } catch (err) {
    console.error(err);
  } finally {
    submitLoading.value = false;
  }
}

// 点击新增
function handleAdd() {
  isEdit.value = false;
  Object.assign(formModel, { id: null, userName: '', realName: '', phone: '', password: '', roleIds:[] });
  showModal.value = true;
}

// 点击编辑
function handleEdit(row: any) {
  isEdit.value = true;
  Object.assign(formModel, {
    id: row.id,
    userName: row.userName,
    realName: row.realName,
    phone: row.phone || '',
    password: '',
    roleIds: row.roles ? row.roles.map((r: any) => r.id) :[]
  });
  showModal.value = true;
}

// 点击删除
async function handleDelete(id: number) {
  window.$dialog?.warning({
    title: '确认删除',
    content: '确定要删除该用户吗？此操作不可逆。',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      const { error } = await fetchDeleteUser(id);
      if (!error) {
        message.success('删除成功');
        init();
      }
    }
  });
}

// 生命周期挂载
onMounted(() => {
  init();
  // 获取角色下拉列表
  fetchAllRoles(-1, 10).then(({ data }) => {
    if (data) {
      roleOptions.value = data.list.map((i: any) => ({ label: i.roleName, value: i.id }));
    }
  });
});
</script>
