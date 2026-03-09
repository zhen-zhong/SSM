<template>
  <div class="h-full flex-col">
    <n-card class="flex-1 shadow-sm h-full" content-class="flex-col">
      <template #header>
        <div class="flex items-center justify-between">
          <span class="font-bold">菜单权限配置</span>
          <div class="flex items-center gap-4">
            <span class="text-gray-500 text-sm">选择角色：</span>
            <n-select
              v-model:value="currentRoleId"
              :options="roleOptions"
              :disabled="!isAdmin"
              @update:value="handleRoleChange"
              class="w-200px"
              placeholder="请选择角色"
            />
            <n-button
              type="primary"
              :disabled="!isAdmin"
              :loading="submitLoading"
              @click="handleSaveMenus"
            >
              保存权限
            </n-button>
          </div>
        </div>
      </template>

      <div class="flex-1 overflow-y-auto mt-2">
        <n-spin :show="loading">
          <n-tree
            :data="menuTree"
            v-model:checked-keys="checkedKeys"
            @update:indeterminate-keys="handleIndeterminateKeys"
            key-field="id"
            label-field="label"
            children-field="children"
            checkable
            cascade
            check-strategy="all"
            default-expand-all
            :disabled="!isAdmin"
          />
        </n-spin>
      </div>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useMessage } from 'naive-ui';
import { useAuthStore } from '@/store/modules/auth';
// 注意：请确保这些 API 方法在你的 @/service/api/system 中已经正确定义
import {
  fetchGetAllMenuTree,
  fetchRoleList,
  fetchGetRoleMenuIds,
  fetchAssignRoleMenus
} from '@/service/api/system';

const message = useMessage();
const authStore = useAuthStore();

const isAdmin = computed(() => {
  return authStore.userInfo.roles.includes('admin');
});

const loading = ref(false);
const submitLoading = ref(false);

const menuTree = ref([]);
const roleOptions = ref<Array<{ label: string; value: number; roleCode: string }>>([]);
const currentRoleId = ref<number | null>(null);

// 🌟 分别存储全选节点和半选节点
const checkedKeys = ref<number[]>([]);
const indeterminateKeys = ref<number[]>([]);

async function initData() {
  loading.value = true;
  try {
    const [menuRes, roleRes] = await Promise.all([
      fetchGetAllMenuTree(),
      fetchRoleList(-1, 10)
    ]);

    if (menuRes.data) menuTree.value = menuRes.data;

    if (roleRes.data && roleRes.data.list) {
      roleOptions.value = roleRes.data.list.map((item: any) => ({
        label: item.roleName,
        value: item.id,
        roleCode: item.roleCode
      }));

      if (roleOptions.value.length > 0) {
        currentRoleId.value = roleOptions.value[0].value;
        await handleRoleChange(currentRoleId.value);
      }
    }
  } finally {
    loading.value = false;
  }
}

// 🌟 监听树组件的半选节点更新
function handleIndeterminateKeys(keys: Array<string | number>) {
  indeterminateKeys.value = keys.map(Number);
}

async function handleRoleChange(roleId: number | null) {
  if (!roleId) return;
  loading.value = true;
  try {
    const { data } = await fetchGetRoleMenuIds(roleId);
    // 回显后端传来的菜单 ID
    checkedKeys.value = (data || []).map(Number);
  } finally {
    loading.value = false;
  }
}

async function handleSaveMenus() {
  if (!currentRoleId.value) return;
  submitLoading.value = true;
  try {
    const finalMenuIds = Array.from(new Set([...checkedKeys.value, ...indeterminateKeys.value]));

    await fetchAssignRoleMenus({
      roleId: currentRoleId.value,
      menuIds: finalMenuIds
    });
    message.success('菜单权限更新成功');
  } finally {
    submitLoading.value = false;
  }
}

onMounted(() => {
  initData();
});
</script>
