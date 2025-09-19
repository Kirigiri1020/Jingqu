<template>
  <view class="admin-dropdown-container">
    <!-- 齿轮图标 -->
    <view class="gear-icon" @click="toggleDropdown">
      <uni-icons type="gear" size="30" color="#fff"></uni-icons>
    </view>
    
    <!-- 下拉列表 -->
    <view v-if="showDropdown" class="dropdown-menu" :style="{ top: dropdownPosition.top + 'px', left: dropdownPosition.left + 'px' }">
      <view class="dropdown-item" @click="checkAdminPermission">
        <text>管理员功能</text>
      </view>
    </view>

    <!-- 管理员功能面板 -->
    <up-popup :show="showAdminPanel" mode="right" @close="showAdminPanel = false">
      <view class="admin-panel">
        <view class="admin-header">
          <text class="admin-title">管理员功能</text>
          <up-icon name="close" @click="showAdminPanel = false"></up-icon>
        </view>
        <view class="admin-menu">
          <view class="admin-item" @click="handleAdminMenu('user')">
            <up-icon name="account" size="20"></up-icon>
            <text>用户管理</text>
          </view>
          <view class="admin-item" @click="handleAdminMenu('scenic')">
            <up-icon name="map" size="20"></up-icon>
            <text>景区管理</text>
          </view>
          <view class="admin-item" @click="handleAdminMenu('project')">
            <up-icon name="list" size="20"></up-icon>
            <text>项目管理</text>
          </view>
        </view>
      </view>
    </up-popup>
  </view>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue'
import { isadmin } from '../api/api.js'

const showDropdown = ref(false)
const showAdminPanel = ref(false)
const dropdownPosition = ref({
  top: 0,
  left: 0
})

const instance = getCurrentInstance()

const toggleDropdown = () => {
  if (showDropdown.value) {
    showDropdown.value = false
    return
  }
  
  // 获取齿轮图标的位置
  const query = uni.createSelectorQuery().in(instance)
  query.select('.gear-icon').boundingClientRect(data => {
    if (data) {
      // 计算下拉菜单的位置，确保不会超出屏幕右侧
      const dropdownWidth = 160 // 下拉菜单的预估宽度
      let leftPosition = data.left
      
      // 检查是否会超出屏幕右侧
      const screenWidth = uni.getSystemInfoSync().windowWidth
      if (leftPosition + dropdownWidth > screenWidth) {
        leftPosition = screenWidth - dropdownWidth + 10 // 留出10px的边距
      }
      
      dropdownPosition.value = {
        top: data.bottom + 5, // 在齿轮下方5px
        left: leftPosition  // 向右移动10px
      }
      showDropdown.value = true
    }
  }).exec()
}

const checkAdminPermission = async () => {
  showDropdown.value = false
  
  try {
    // 调用isadmin接口检查管理员权限
    const response = await isadmin()
    
    if (response.isadmin) {
      // 是管理员，显示管理员功能面板
      showAdminPanel.value = true
    } else {
      // 不是管理员，显示提示信息
      uni.showToast({
        title: '您没有管理员权限',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('检查管理员权限失败:', error)
    uni.showToast({
      title: '权限检查失败',
      icon: 'error'
    })
  }
}

const handleAdminMenu = (type) => {
  console.log('选择的管理功能:', type)
  showAdminPanel.value = false
  
  // 根据不同的type跳转到相应的页面
  const routes = {
    user: `/pages/admin/user`,
    scenic: `/pages/admin/jingqu`,
    project: `/pages/admin/project`
  }
  
  if (routes[type]) {
    uni.navigateTo({
      url: routes[type]
    })
  } else {
    uni.showToast({
      title: `进入${getMenuName(type)}`,
      icon: 'none'
    })
  }
}

const getMenuName = (type) => {
  const menuMap = {
    user: '用户管理',
    scenic: '景区管理', 
    project: '项目管理'
  }
  return menuMap[type] || '未知功能'
}

// 点击其他地方关闭下拉菜单
const handleClickOutside = () => {
  if (showDropdown.value) {
    showDropdown.value = false
  }
}

onMounted(() => {
  // 在Uni-app中，使用uni.$on来监听全局点击事件
  uni.$on('clickOutside', handleClickOutside)
})

onBeforeUnmount(() => {
  // 移除事件监听
  uni.$off('clickOutside', handleClickOutside)
})
</script>

<style scoped>
.admin-dropdown-container {
  position: relative;
  display: inline-block;
}

.gear-icon {
  padding: 8rpx;
  cursor: pointer;
}

.dropdown-menu {
  position: fixed;
  background: #fff;
  border-radius: 8rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
  z-index: 9999;
  min-width: 160rpx;
  padding: 16rpx 0;
}

.dropdown-item {
  padding: 20rpx 24rpx;
  font-size: 28rpx;
  color: #333;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.dropdown-item text {
  display: block;
}

/* 管理员面板样式 */
.admin-panel {
  width: 150px;
  height: 100vh;
  background: #fff;
  padding: 20px;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.admin-title {
  font-size: 18px;
  font-weight: bold;
}

.admin-menu {
  margin-top: 30px;
}

.admin-item {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  margin-bottom: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.admin-item:hover {
  background-color: #f5f5f5;
}

.admin-item up-icon {
  margin-right: 10px;
}

.admin-item text {
  font-size: 16px;
}
</style>
