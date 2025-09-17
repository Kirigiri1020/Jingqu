<template>
  <view class="profile-container">

    <view class="profile-content">
      <!-- 头像上传 -->
      <view class="avatar-section">
        <button class="avatar-wrapper" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
          <image class="avatar" :src="formData.avatarUrl || '../../static/logo.png'" mode="aspectFill" />
          <uni-icons class="camera-icon" type="camera" size="16" color="#fff"></uni-icons>
        </button>
      </view>

      <!-- 昵称输入 -->
      <view class="profile-item">
        <view class="item-label">昵称</view>
        <view class="item-value">
          <input 
            v-model="formData.nickName" 
            type="nickname" 
            placeholder="请输入昵称" 
            class="nickname-input"
            @input="validateForm"
          />
        </view>
      </view>

      <!-- 邮箱输入 -->
      <view class="profile-item">
        <view class="item-label">邮箱</view>
        <view class="item-value">
          <input 
            v-model="formData.email" 
            type="email" 
            placeholder="请输入邮箱" 
            class="email-input"
            @input="validateForm"
          />
        </view>
      </view>

      <!-- 电话号码输入 -->
      <view class="profile-item">
        <view class="item-label">电话</view>
        <view class="item-value">
          <input 
            v-model="formData.phone" 
            type="number" 
            placeholder="请输入电话号码" 
            class="phone-input"
            @input="validateForm"
          />
        </view>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="save-button-container">
      <button 
        class="save-button" 
        :class="{ disabled: !isFormValid || !hasChanges }" 
        :disabled="!isFormValid || !hasChanges"
        @click="handleSave"
      >
        保存
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

// 表单数据
const formData = reactive({
  avatarUrl: '',
  nickName: '',
  email: '',
  phone: ''
})

// 原始数据用于比较是否有变化
const originalData = reactive({})
const isFormValid = ref(false)
const hasChanges = ref(false)

onLoad(() => {
  // 从本地存储加载用户信息
  loadUserInfo()
})

// 加载用户信息
const loadUserInfo = () => {
  const userInfo = uni.getStorageSync('userInfo')
  if (userInfo) {
    const parsedInfo = JSON.parse(userInfo)
    // 设置表单数据
    Object.assign(formData, {
      avatarUrl: parsedInfo.avatarUrl || '',
      nickName: parsedInfo.nickName || '',
      email: parsedInfo.email || '',
      phone: parsedInfo.phone || ''
    })
    // 保存原始数据用于比较
    Object.assign(originalData, { ...formData })
  }
  validateForm()
}

// 选择头像
const onChooseAvatar = (e) => {
  formData.avatarUrl = e.detail.avatarUrl
  validateForm()
  checkChanges()
}

// 验证表单
const validateForm = () => {
  // 昵称不能为空
  isFormValid.value = !!formData.nickName && formData.nickName.trim() !== ''
  checkChanges()
}

// 检查是否有变化
const checkChanges = () => {
  hasChanges.value = 
    formData.avatarUrl !== originalData.avatarUrl ||
    formData.nickName !== originalData.nickName ||
    formData.email !== originalData.email ||
    formData.phone !== originalData.phone
}

// 保存信息
const handleSave = () => {
  if (!isFormValid.value || !hasChanges.value) return

  try {
    // 保存到本地存储
    const userInfo = {
      avatarUrl: formData.avatarUrl,
      nickName: formData.nickName,
      email: formData.email,
      phone: formData.phone
    }
    uni.setStorageSync('userInfo', JSON.stringify(userInfo))
    
    // 更新原始数据
    Object.assign(originalData, { ...formData })
    hasChanges.value = false
    
    uni.showToast({
      title: '保存成功',
      icon: 'success',
      duration: 2000
    })
  } catch (error) {
    uni.showToast({
      title: '保存失败',
      icon: 'error',
      duration: 2000
    })
  }
}

// 监听表单变化
watch(formData, () => {
  checkChanges()
}, { deep: true })
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.profile-content {
  background-color: #fff;
  margin-top: 20rpx;
}

.avatar-section {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60rpx 30rpx 40rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.profile-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
  
  .item-label {
    font-size: 32rpx;
    color: #333;
    min-width: 120rpx;
  }
  
  .item-value {
    flex: 1;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
}

.avatar-wrapper {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  border: none;
  padding: 0;
  background: transparent;
  
  .avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background-color: #f0f0f0;
  }
  
  .camera-icon {
    position: absolute;
    right: 4rpx;
    bottom: 4rpx;
    background-color: rgba(0, 0, 0, 0.6);
    border-radius: 50%;
    width: 40rpx;
    height: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 10;
  }
}

.nickname-input,
.email-input,
.phone-input {
  text-align: right;
  font-size: 32rpx;
  color: #333;
  width: 100%;
  border: none;
  outline: none;
  
  &::placeholder {
    color: #999;
  }
}

.save-button-container {
  padding: 40rpx 30rpx;
  margin-top: 40rpx;
  
  .save-button {
    width: 100%;
    height: 88rpx;
    background-color: #e74c3c;
    color: #fff;
    font-size: 32rpx;
    border-radius: 44rpx;
    border: none;
    
    &.disabled {
      background-color: #ccc;
      color: #999;
    }
    
    &:not(.disabled):active {
      background-color: #c0392b;
    }
  }
}
</style>
