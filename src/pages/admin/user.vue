<template>
  <view class="user-management">
    <view class="header">
      <text class="title">用户管理</text>
    </view>

    <view class="table-container">
      <scroll-view class="table-scroll" scroll-x="true">
        <view class="table">
          <!-- 表头 -->
          <view class="table-header">
            <view class="header-cell" v-for="column in columns" :key="column.key" :style="{ width: column.width + 'px' }">
              <text>{{ column.title }}</text>
            </view>
            <view class="header-cell" style="width: 200px">
              <text>操作</text>
            </view>
          </view>

          <!-- 表格内容 -->
          <view class="table-body">
            <view class="table-row" v-for="(user, index) in userList" :key="user.id">
              <view class="table-cell" v-for="column in columns" :key="column.key" :style="{ width: column.width + 'px' }">
                <up-input
                  v-model="user[column.key]"
                  :type="getInputType(column.key)"
                  border="none"
                  :disabled="!user.editing || ['id', 'createtime', 'updatetime'].includes(column.key)"
                  @blur="handleInputBlur(user, column.key)"
                />
              </view>
              <view class="table-cell actions" style="width: 200px">
                <up-button 
                  v-if="!user.editing"
                  type="primary" 
                  size="mini" 
                  @click="startEditing(user)"
                >
                  修改数据
                </up-button>
                <up-button 
                  v-else
                  type="success" 
                  size="mini" 
                  @click="confirmSave(user)"
                >
                  保存
                </up-button>
                <up-button 
                  type="error" 
                  size="mini" 
                  @click="confirmDelete(user.id)"
                  :disabled="user.editing"
                >
                  删除
                </up-button>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 加载状态 -->
    <view class="loading" v-if="loading">
      <text>加载中...</text>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-if="!loading && userList.length === 0">
      <text>暂无用户数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getalluserinfo, updateeuserinfo, deleteuserinfo } from '../../api/api.js'

interface User {
  id: number
  nickname: string
  avatarurl: string
  email: string | null
  phone: string | null
  createtime: string
  updatetime: string
  isadmin: boolean
  editing?: boolean
  originalData?: any
}

const loading = ref(false)
const userList = ref<User[]>([])

const columns = [
  { key: 'id', title: 'ID', width: 80 },
  { key: 'nickname', title: '昵称', width: 120 },
  { key: 'avatarurl', title: '头像', width: 150 },
  { key: 'email', title: '邮箱', width: 150 },
  { key: 'phone', title: '手机', width: 120 },
  { key: 'createtime', title: '创建时间', width: 180 },
  { key: 'updatetime', title: '更新时间', width: 180 },
  { key: 'isadmin', title: '管理员', width: 100 }
]

const getInputType = (key: string) => {
  const typeMap: { [key: string]: string } = {
    id: 'number',
    email: 'email',
    phone: 'number',
    isadmin: 'text'
  }
  return typeMap[key] || 'text'
}

// 获取用户数据
const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await getalluserinfo()
    userList.value = (response.users || []).map(user => ({
      ...user,
      editing: false,
      originalData: null
    }))
  } catch (error) {
    console.error('获取用户数据失败:', error)
    uni.showToast({
      title: '获取用户数据失败',
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}

// 开始编辑
const startEditing = (user: User) => {
  userList.value.forEach(u => {
    if (u.id !== user.id && u.editing) {
      cancelEditing(u)
    }
  })
  
  user.editing = true
  user.originalData = { ...user }
}

// 取消编辑
const cancelEditing = (user: User) => {
  if (user.originalData) {
    Object.assign(user, user.originalData)
  }
  user.editing = false
  user.originalData = null
}

// 输入框失去焦点处理
const handleInputBlur = (user: User, key: string) => {
  // 这里可以添加一些验证逻辑
}

// 确认保存
const confirmSave = (user: User) => {
  uni.showModal({
    title: '确认',
    content: '是否确定更改数据？',
    success: (res) => {
      if (res.confirm) {
        saveUser(user)
      }
    }
  })
}

// 保存用户修改
const saveUser = async (user: User) => {
  try {
    // 构建要提交的数据对象，排除编辑状态相关字段
    const userData = {
      id: user.id,
      nickname: user.nickname,
      avatarurl: user.avatarurl,
      email: user.email,
      phone: user.phone,
      isadmin: user.isadmin
    }
    
    console.log('准备发送的用户数据:', userData)
    
    await updateeuserinfo(userData)
    user.editing = false
    user.originalData = null
    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })
    // 重新加载数据以确保数据最新
    await fetchUsers()
  } catch (error) {
    console.error('保存用户信息失败:', error)
    console.error('错误详情:', error)
    // 显示具体的错误信息
    uni.showToast({
      title: error.message || '保存失败',
      icon: 'error',
      duration: 3000
    })
  }
}

// 确认删除
const confirmDelete = (userId: number) => {
  uni.showModal({
    title: '确认删除',
    content: '是否确定删除该用户？',
    success: (res) => {
      if (res.confirm) {
        deleteUser(userId)
      }
    }
  })
}

// 删除用户
const deleteUser = async (userId: number) => {
  try {
    await deleteuserinfo(userId)
    uni.showToast({
      title: '删除成功',
      icon: 'success'
    })
    // 重新加载数据
    await fetchUsers()
  } catch (error) {
    console.error('删除用户失败:', error)
    uni.showToast({
      title: '删除失败',
      icon: 'error'
    })
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.user-management {
  padding: 20rpx;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  padding: 20rpx;
  background: #fff;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);

  .title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
  }
}

.table-container {
  background: #fff;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.table-scroll {
  width: 100%;
  max-width: 100vw;
}

.table {
  min-width: 1300px; // 增加宽度以适应操作列
}

.table-header,
.table-row {
  display: flex;
  border-bottom: 1rpx solid #eee;
}

.header-cell {
  padding: 20rpx;
  background-color: #f8f9fa;
  font-weight: bold;
  color: #333;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.table-cell {
  padding: 15rpx;
  display: flex;
  align-items: center;
  min-height: 80rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  :deep(.u-input) {
    width: 100%;
    min-height: 60rpx;
  }

  :deep(.u-input__content__field-wrapper__field) {
    padding: 0 10rpx;
    font-size: 28rpx;
  }

  :deep(.u-input__content__field-wrapper__field:disabled) {
    background-color: transparent;
    color: #333;
    opacity: 1;
  }
}

.actions {
  display: flex;
  justify-content: center;
  gap: 10rpx;
  flex-wrap: wrap;
}

.empty {
  margin-top: 100rpx;
  text-align: center;
  color: #999;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 100rpx 0;
  color: #999;
}
</style>
