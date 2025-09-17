<template>
  <view class="content">
    <!-- 搜索框和筛选面板 -->
    <view class="search-container" @click="handleSearchClick">
      <up-search 
        placeholder="搜索景点" 
        bg-color="#e3e3e3" 
        v-model="keyword"
        @clear="handleSearchClear"
        @change="handleSearchChange"
      ></up-search>
    </view>

    <!-- 使用uView的popup组件显示筛选面板 -->
    <up-popup :show="showFilter" mode="top" :round="10" @close="showFilter = false" :z-index="9999">
      <view class="popup-content">
        <SearchFilter 
          :show-filter="showFilter" 
          @filter-change="handleFilterChange"
          @close-panel="showFilter = false"
        />
      </view>
    </up-popup>

    <up-swiper v-if="bannerList.length" :list="bannerList" keyName="image" showTitle redius='8' :autoplay='true'
      height='160'></up-swiper>
    <up-notice-bar text="项目数据仅为示例,非真实数据"></up-notice-bar>
    
    <!-- 筛选结果提示 -->
    <view v-if="hasActiveFiltersComputed" class="filter-result-tip">
      <text>已应用筛选条件</text>
      <up-icon name="close" size="16" color="#999" @click="clearAllFilters" />
    </view>

    <view class="list">
      <up-waterfall v-model="filteredFullList" ref="uWaterfallRef" :key="filterKey" v-if="showWaterfall">
        <template v-slot:left="{ leftList }">
          <view class="demo-water" v-for="(item, index) in leftList" :key='index' @click="goDetail(item)">
            <up-lazy-load threshold="-100" border-radius='10' :image="item.img" :index="index"></up-lazy-load>
            <view class="demo-title">
              {{ item.title }}
            </view>
            <view class="demo-info">
              <text class="price">¥{{ item.price }}</text>
              <text class="count">{{ item.count }}分</text>
              <text class="place">{{ item.place }}</text>
            </view>
            <view class="demo-price">
              {{ item.times }}
            </view>
            <view class="demo-tag">
              <view class="demo-tag-owner">
                {{ item.tag[0] }}
              </view>
              <view class="demo-tag-text">
                {{ item.tag[1] }}
              </view>
            </view>
            <view class="isDot" v-if='item.isDot'>
              {{ item.isDot }}
            </view>
          </view>
        </template>
        <template v-slot:right="{ rightList }">
          <view class="demo-water" v-for="(item, index) in rightList" :key='index' @click="goDetail(item)">
            <up-lazy-load threshold="-500" border-radius='10' :image="item.img" :index="index"></up-lazy-load>
            <view class="demo-title">
              {{ item.title }}
            </view>
            <view class="demo-info">
              <text class="price">¥{{ item.price }}</text>
              <text class="count">{{ item.count }}分</text>
              <text class="place">{{ item.place }}</text>
            </view>
            <view class="demo-price">
              {{ item.times }}
            </view>
            <view class="demo-tag">
              <view class="demo-tag-owner">
                {{ item.tag[0] }}
              </view>
              <view class="demo-tag-text">
                {{ item.tag[1] }}
              </view>
            </view>
            <view class="isDot" v-if='item.isDot'>
              {{ item.isDot }}
            </view>
          </view>
        </template>
      </up-waterfall>
    </view>
    
    <!-- 无结果提示 -->
    <view v-if="filteredFullList.length === 0 && originalList.length > 0" class="no-result">
      <up-empty mode="data" icon="http://cdn.uviewui.com/uview/empty/data.png">
        <text class="no-result-text">暂无符合条件的景区</text>
      </up-empty>
    </view>


    <view class="topClass" v-if="showTopBtn" @click="Totop">
      <up-icon name="arrow-upward" color="#fff" size="28"></up-icon>
    </view>
  </view>
</template>

<script setup>
import { getBanner, getHomeList } from '../../api/api.js'
import { onLoad, onReachBottom, onPageScroll } from '@dcloudio/uni-app';
import { ref, computed, watch, nextTick } from 'vue';
import SearchFilter from '../../components/SearchFilter.vue';
import { filterScenicSpots, hasActiveFilters, resetFilters } from '../../utils/filterUtils.js';

const showFilter = ref(false)
const keyword = ref('')
const bannerList = ref([])
const originalList = ref([]) // 原始数据
const currentFilters = ref(resetFilters())
const showTopBtn = ref(0)
const uWaterfallRef = ref(null)
const filterKey = ref(0)
const showWaterfall = ref(true)

// 计算筛选后的完整列表
const filteredFullList = computed(() => {
  if (!hasActiveFilters(currentFilters.value) && !keyword.value) {
    return originalList.value
  }
  
  let filtered = [...originalList.value]
  
  // 先应用筛选条件
  if (hasActiveFilters(currentFilters.value)) {
    filtered = filterScenicSpots(filtered, currentFilters.value)
  }
  
  // 再应用关键词搜索
  if (keyword.value.trim()) {
    const searchTerm = keyword.value.trim()
    filtered = filtered.filter(item => 
      item.title && item.title.includes(searchTerm)
    )
  }
  
  return filtered
})


// 检查是否有激活的筛选条件
const hasActiveFiltersComputed = computed(() => {
  return hasActiveFilters(currentFilters.value)
})

onLoad(() => {
  console.log('onLoad called')
  getBanner().then(res => {
    console.log('getBanner success:', res, 'res')
    bannerList.value = res.bannerList
  }).catch(err => {
    console.error('getBanner error:', err)
  })

  getHomeList().then(res => {
    // 确保获取的是数组数据，正确处理API返回的数据结构
    originalList.value = Array.isArray(res) ? res : (res.data || res.list || [])
  }).catch(err => {
    console.error('getHomeList error:', err)
  })
})

onReachBottom(() => {
  console.log("触底")
})

onPageScroll((e) => {
  if (e.scrollTop > 600) {
    showTopBtn.value = 1
  } else {
    showTopBtn.value = 0
  }
})

const Totop = () => {
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 300
  })
}


const goDetail = (item) => {
  const can = JSON.stringify(item)
  uni.navigateTo({ url: `/pages/detail/detail?item=${encodeURIComponent(can)}` })
}

// 点击搜索框显示筛选面板
const handleSearchClick = () => {
  showFilter.value = true
}

// 清空搜索关键词
const handleSearchClear = () => {
  keyword.value = ''
}

// 处理筛选条件变化
const handleFilterChange = (filters) => {
  currentFilters.value = { ...filters }
  // 不再自动关闭面板，让用户点击确定后再关闭
}

// 清空所有筛选条件
const clearAllFilters = () => {
  currentFilters.value = resetFilters()
  keyword.value = ''
}

// 处理搜索输入变化
const handleSearchChange = (value) => {
  keyword.value = value
  console.log('搜索关键词:', keyword.value)
}

  // 监听筛选结果变化
watch(filteredFullList, (newValue) => {
  console.log('筛选后列表长度:', newValue.length)
  console.log('筛选后列表内容:', newValue)
  
  // 强制重新渲染瀑布流组件（小程序兼容方案）
  showWaterfall.value = false
  nextTick(() => {
    showWaterfall.value = true
  })
}, { immediate: true })


</script>

<style>
page {
  background-color: rgb(240, 240, 240);
}
</style>

<style lang="scss" scoped>
.content {
  padding: 20rpx 20rpx;
  position: relative;
}

.search-container {
  position: relative;
  margin-bottom: 20rpx;
}

.filter-result-tip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #e6f7ff;
  border: 1rpx solid #91d5ff;
  border-radius: 8rpx;
  padding: 16rpx 20rpx;
  margin-bottom: 20rpx;
  font-size: 26rpx;
  color: #1890ff;
}

.no-result {
  padding: 100rpx 0;
  text-align: center;
}

.no-result-text {
  font-size: 28rpx;
  color: #999;
}

  .list {
    margin: 30rpx 0;

    .demo-water {
      margin: 10rpx 10rpx 10rpx 0;
      background-color: #fff;
      border-radius: 16rpx;
      padding: 16rpx;
      position: relative;
    }

  .demo-title {
    font-size: 30rpx;
    margin-top: 10rpx;
    color: #303133;
    font-weight: 600;
  }

  .demo-info {
    display: flex;
    gap: 16rpx;
    margin: 8rpx 0;
    font-size: 24rpx;
    
    .price {
      color: #ff4d4f;
      font-weight: 600;
    }
    
    .count {
      color: #52c41a;
    }
    
    .place {
      color: #1890ff;
    }
  }

  .demo-price {
    font-size: 24rpx;
    color: #777;
    margin-top: 4rpx;
  }

  .demo-tag {
    display: flex;
    margin-top: 10rpx;

    .demo-tag-owner {
      border: 1px solid rgb(252, 163, 129);
      color: #ffaa00;
      font-size: 20rpx;
      display: flex;
      align-items: center;
      padding: 4rpx 14rpx;
      border-radius: 50rpx;
    }

    .demo-tag-text {
      border: 1px solid #00aaff;
      color: #00aaff;
      margin-left: 20rpx;
      font-size: 20rpx;
      display: flex;
      align-items: center;
      padding: 4rpx 14rpx;
      border-radius: 50rpx;
    }
  }

  .isDot {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    font-size: 24rpx;
    color: #fff;
    line-height: 32rpx;
    background-color: #ff0000;
    text-align: center;
    border-radius: 10rpx;
    padding: 4rpx 10rpx;
  }
}

.topClass {
  position: fixed;
  bottom: 120rpx;
  right: 30rpx;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 20rpx;
  width: 44rpx;
  height: 44rpx;
  border-radius: 40rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
