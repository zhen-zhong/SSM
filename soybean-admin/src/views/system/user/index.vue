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
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :scroll-x="1400"
        remote />
    </n-card>

    <n-modal v-model:show="showModal" preset="card" :title="isEdit ? '编辑用户' : '新增用户'" class="w-550px">
      <n-form ref="formRef" :model="formModel" :rules="rules" label-placement="left" label-width="80"
        require-mark-placement="right-hanging">
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
          <n-input v-model:value="formModel.password" type="password" show-password-on="click"
            :placeholder="isEdit ? '若不修改请留空' : '请输入初始密码'" />
        </n-form-item>

        <n-form-item label="分配角色" path="roleIds">
          <n-select v-model:value="formModel.roleIds" multiple :options="roleOptions" placeholder="请选择权限角色" />
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
import { fetchUserList, fetchAddUser, fetchUpdateUser, fetchDeleteUser, fetchAssignRole, fetchResetPassword, fetchAllRoles } from '@/service/api/system';

const message = useMessage();
const formRef = ref<FormInst | null>(null);

// --- 状态变量 ---
const loading = ref(false);
const submitLoading = ref(false);
const showModal = ref(false);
const isEdit = ref(false);
const tableData = ref([]);
const roleOptions = ref<{ label: string; value: number }[]>([]);

// --- 表单模型 ---
const formModel = reactive({
  id: null as number | null,
  userName: '', // 🌟 对齐后端 JsonProperty("userName")
  realName: '',
  phone: '',
  password: '',
  roleIds: [] as number[]
});

// --- 校验规则 ---
const rules = {
  userName: { required: true, message: '请输入用户名', trigger: 'blur' },
  realName: { required: true, message: '请输入真实姓名', trigger: 'blur' },
  password: { required: true, message: '请输入初始密码', trigger: 'blur' },
  phone: { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'input' }
};

// --- 表格列定义 ---
const columns: DataTableColumns<any> = [
  { title: 'ID', key: 'id', width: 70, align: 'center', fixed: 'left' },
  { title: '登录账号', key: 'userName', width: 120, align: 'center' },
  { title: '真实姓名', key: 'realName', width: 120, align: 'center' },
  { title: '手机号', key: 'phone', width: 130, align: 'center', render: (row) => row.phone || '--' },
  {
    title: '角色权限',
    key: 'roles',
    width: 200,
    align: 'center',
    render: (row) => {
      if (!row.roles || row.roles.length === 0) return <span class="text-gray-400" > 暂无 </span>;
      return (
        <NSpace size= { [4, 4]} justify = "center" >
        {
          row.roles.map((role: any) => (
            <NTag type= "primary" size = "small" bordered = { false} key = { role.id } > { role.roleName } </NTag>
          ))
        }
          </NSpace>
      );
    }
  },
{
  title: '状态',
    key: 'status',
      width: 100,
        align: 'center',
          render: (row) => <NTag type={ row.status === 1 ? 'success' : 'error' }> { row.status === 1 ? '正常' : '停用' } </NTag>
},
{ title: '创建时间', key: 'createTime', width: 170, align: 'center' },
{ title: '更新时间', key: 'updateTime', width: 170, align: 'center' },
{
  title: '操作',
    key: 'actions',
      width: 160,
        align: 'center',
          fixed: 'right',
            render: (row) => (
              <NSpace justify= "center" >
              <NButton size="small" ghost onClick = {() => handleEdit(row)
}> 编辑 </NButton>
  < NButton size = "small" type = "error" ghost onClick = {() => handleDelete(row.id)}> 删除 </NButton>
    </NSpace>
    )
  }
];

const pagination = reactive({ page: 1, pageSize: 10, showSizePicker: true });


async function getRoleOptions() {
  const { data } = await fetchAllRoles();
  if (data) {
    roleOptions.value = data.map((item: any) => ({
      label: item.roleName,
      value: item.id
    }));
  }
}

async function init() {
  loading.value = true;
  const { data } = await fetchUserList();
  if (data) tableData.value = data;
  loading.value = false;
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(formModel, { id: null, userName: '', realName: '', phone: '', password: '', roleIds: [] });
  showModal.value = true;
}

function handleEdit(row: any) {
  isEdit.value = true;
  Object.assign(formModel, {
    id: row.id,
    userName: row.userName,
    realName: row.realName,
    phone: row.phone || '',
    password: '',
    roleIds: row.roles ? row.roles.map((r: any) => r.id) : []
  });
  showModal.value = true;
}

async function handleSave() {
  await formRef.value?.validate();
  submitLoading.value = true;

  try {
    const { data, error } = isEdit.value
      ? await fetchUpdateUser(formModel)
      : await fetchAddUser(formModel);

    if (error) {
      return;
    }
    let currentUserId = formModel.id;
    if (isEdit.value && formModel.password && currentUserId) {
      await fetchResetPassword(currentUserId, formModel.password);
    }

    if (currentUserId && formModel.roleIds.length > 0) {
      await fetchAssignRole(currentUserId, formModel.roleIds);
    }

    message.success(typeof data === 'string' ? data : (isEdit.value ? '修改成功' : '新增成功'));

    showModal.value = false;
    init();

  } catch (err) {
    console.error('Submit Error:', err);
  } finally {
    submitLoading.value = false;
  }
}

async function handleDelete(id: number) {
  const { error } = await fetchDeleteUser(id);
  if (!error) {
    message.success('删除成功');
    init();
  }
}

onMounted(() => {
  init();
  getRoleOptions();
});
</script>
