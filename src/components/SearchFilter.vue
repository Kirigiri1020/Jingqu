<template>
  <view class="search-filter" v-if="showFilter">
    <view class="filter-content">
      <!-- 景区搜索 -->
      <view class="filter-item">
        <text class="filter-label">景区搜索：</text>
        <up-input
          v-model="filters.title"
          placeholder="输入景区名称"
          clearable
        ></up-input>
      </view>

      <!-- 价格范围 -->
      <view class="filter-item">
        <text class="filter-label">价格范围：</text>
        <view class="range-inputs">
          <up-input
            v-model="filters.minPrice"
            placeholder="最低价"
            type="number"
            clearable
          ></up-input>
          <text class="range-separator">-</text>
          <up-input
            v-model="filters.maxPrice"
            placeholder="最高价"
            type="number"
            clearable
          ></up-input>
          <text class="range-unit">元</text>
        </view>
      </view>

      <!-- 评分范围 -->
      <view class="filter-item">
        <text class="filter-label">评分范围：</text>
        <view class="range-inputs">
          <up-input
            v-model="filters.minCount"
            placeholder="最低分"
            type="number"
            clearable
          ></up-input>
          <text class="range-separator">-</text>
          <up-input
            v-model="filters.maxCount"
            placeholder="最高分"
            type="number"
            clearable
          ></up-input>
          <text class="range-unit">分</text>
        </view>
      </view>

      <!-- 地理位置 -->
      <view class="filter-item">
        <text class="filter-label">地理位置：</text>
        <up-input
          v-model="filters.address"
          placeholder="（省份，直辖市，自治区）"
          clearable
        ></up-input>
      </view>

      <!-- 操作按钮 -->
      <view class="filter-actions">
        <up-button type="default" size="mini" @click="resetFilters">重置</up-button>
        <up-button type="primary" size="mini" @click="applyFilters">确定</up-button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue'

const props = defineProps({
  showFilter: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['filter-change', 'close-panel'])

const filters = ref({
  title: '',
  minPrice: '',
  maxPrice: '',
  minCount: '',
  maxCount: '',
  address: ''
})

// 应用筛选条件
const applyFilters = () => {
  emit('filter-change', filters.value)
  // 触发关闭事件，让父组件关闭面板
  emit('close-panel')
}

// 重置筛选条件（只清空输入，不触发筛选）
const resetFilters = () => {
  filters.value = {
    title: '',
    minPrice: '',
    maxPrice: '',
    minCount: '',
    maxCount: '',
    address: ''
  }
}

// 监听显示状态变化，只在面板关闭时重置筛选条件
watch(() => props.showFilter, (newVal) => {
  if (!newVal) {
    resetFilters()
  }
})
</script>

<style lang="scss" scoped>
.search-filter {
  background: white;
}

.filter-content {
  padding: 24rpx;
  max-height: 70vh;
  overflow-y: auto;
}

.filter-item {
  margin-bottom: 24rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.filter-label {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 12rpx;
}

.range-inputs {
  display: flex;
  align-items: center;
  gap: 16rpx;
  
  :deep(.u-input) {
    flex: 1;
    min-width: 0;
  }
}

.range-separator {
  color: #999;
  font-size: 28rpx;
  margin: 0 8rpx;
}

.range-unit {
  color: #666;
  font-size: 24rpx;
  white-space: nowrap;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
  margin-top: 24rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #eee;
  
  :deep(.u-button) {
    height: 60rpx;
    font-size: 28rpx;
    line-height: 60rpx;
  }
}
</style>
