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
    
    <!-- 评分排序按钮 -->
    <view class="sort-button-container">
      <view 
        :class="['sort-button', isSortedByRating ? 'sort-button-active' : '']"
        @click="toggleRatingSort"
      >
        <text>按评分降序</text>
        <up-icon 
          v-if="isSortedByRating" 
          name="arrow-down" 
          size="16" 
          color="#1890ff" 
        />
      </view>
    </view>
    
    <!-- 筛选结果提示 -->
    <view v-if="hasActiveFiltersComputed" class="filter-result-tip">
      <text>已应用筛选条件</text>
      <up-icon name="close" size="16" color="#999" @click="clearAllFilters" />
    </view>

    <view class="list">
      <!-- 普通双列布局（用于排序后的显示） -->
      <view v-if="isSortedByRating" class="two-column-layout">
        <view class="column">
          <view class="demo-water" v-for="(item, index) in leftColumnList" :key='index' @click="goDetailFromSortedList(item)">
            <up-lazy-load threshold="-100" border-radius='10' :image="item.image" :index="index"></up-lazy-load>
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
            <view class="demo-tag" v-if="item.tags && item.tags.length">
              <view class="demo-tag-owner" v-if="item.tags[0]">
                {{ item.tags[0] }}
              </view>
              <view class="demo-tag-text" v-if="item.tags[1]">
                {{ item.tags[1] }}
              </view>
            </view>
            <view class="isDot" v-if='item.isDot'>
              {{ item.isDot }}
            </view>
          </view>
        </view>
        <view class="column">
          <view class="demo-water" v-for="(item, index) in rightColumnList" :key='index' @click="goDetailFromSortedList(item)">
            <up-lazy-load threshold="-100" border-radius='10' :image="item.image" :index="index"></up-lazy-load>
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
            <view class="demo-tag" v-if="item.tags && item.tags.length">
              <view class="demo-tag-owner" v-if="item.tags[0]">
                {{ item.tags[0] }}
              </view>
              <view class="demo-tag-text" v-if="item.tags[1]">
                {{ item.tags[1] }}
              </view>
            </view>
            <view class="isDot" v-if='item.isDot'>
              {{ item.isDot }}
            </view>
          </view>
        </view>
      </view>

      <!-- 未筛选状态下的瀑布流 -->
      <template v-else-if="!hasActiveFiltersComputed">
        <up-waterfall v-model="filteredFullList" ref="uWaterfallRef" :key="filterKey">
          <template v-slot:left="{ leftList }">
            <view class="demo-water" v-for="(item, index) in leftList" :key='index' @click="goDetail(item)">
              <up-lazy-load threshold="-100" border-radius='10' :image="item.image || item.img" :index="index"></up-lazy-load>
              <view class="demo-title">
                {{ item.title }}
              </view>
              <view class="demo-info">
                <text class="price">¥{{ item.price }}</text>
                <text class="count">{{ item.count }}分</text>
                <text class="place">{{ item.place || item.address }}</text>
              </view>
              <view class="demo-price">
                {{ item.times }}
              </view>
              <view class="demo-tag" v-if="(item.tags && item.tags.length) || (item.tag && item.tag.length)">
                <view class="demo-tag-owner" v-if="(item.tags && item.tags[0]) || (item.tag && item.tag[0])">
                  {{ (item.tags && item.tags[0]) || (item.tag && item.tag[0]) }}
                </view>
                <view class="demo-tag-text" v-if="(item.tags && item.tags[1]) || (item.tag && item.tag[1])">
                  {{ (item.tags && item.tags[1]) || (item.tag && item.tag[1]) }}
                </view>
              </view>
              <view class="isDot" v-if='item.isDot'>
                {{ item.isDot }}
              </view>
            </view>
          </template>
          <template v-slot:right="{ rightList }">
            <view class="demo-water" v-for="(item, index) in rightList" :key='index' @click="goDetail(item)">
              <up-lazy-load threshold="-500" border-radius='10' :image="item.image || item.img" :index="index"></up-lazy-load>
              <view class="demo-title">
                {{ item.title }}
              </view>
              <view class="demo-info">
                <text class="price">¥{{ item.price }}</text>
                <text class="count">{{ item.count }}分</text>
                <text class="place">{{ item.place || item.address }}</text>
              </view>
              <view class="demo-price">
                {{ item.times }}
              </view>
              <view class="demo-tag" v-if="(item.tags && item.tags.length) || (item.tag && item.tag.length)">
                <view class="demo-tag-owner" v-if="(item.tags && item.tags[0]) || (item.tag && item.tag[0])">
                  {{ (item.tags && item.tags[0]) || (item.tag && item.tag[0]) }}
                </view>
                <view class="demo-tag-text" v-if="(item.tags && item.tags[1]) || (item.tag && item.tag[1])">
                  {{ (item.tags && item.tags[1]) || (item.tag && item.tag[1]) }}
                </view>
              </view>
              <view class="isDot" v-if='item.isDot'>
                {{ item.isDot }}
              </view>
            </view>
          </template>
        </up-waterfall>
      </template>

      <!-- 筛选状态下的瀑布流 -->
      <template v-else>
        <up-waterfall v-model="filteredFullList" ref="uWaterfallRefFiltered" :key="filterKey + 'filtered'">
          <template v-slot:left="{ leftList }">
            <view class="demo-water" v-for="(item, index) in leftList" :key='index' @click="goDetail(item)">
              <up-lazy-load threshold="-100" border-radius='10' :image="item.image || item.img" :index="index"></up-lazy-load>
              <view class="demo-title">
                {{ item.title }}
              </view>
              <view class="demo-info">
                <text class="price">¥{{ item.price }}</text>
                <text class="count">{{ item.count }}分</text>
                <text class="place">{{ item.place || item.address }}</text>
              </view>
              <view class="demo-price">
                {{ item.times }}
              </view>
              <view class="demo-tag" v-if="(item.tags && item.tags.length) || (item.tag && item.tag.length)">
                <view class="demo-tag-owner" v-if="(item.tags && item.tags[0]) || (item.tag && item.tag[0])">
                  {{ (item.tags && item.tags[0]) || (item.tag && item.tag[0]) }}
                </view>
                <view class="demo-tag-text" v-if="(item.tags && item.tags[1]) || (item.tag && item.tag[1])">
                  {{ (item.tags && item.tags[1]) || (item.tag && item.tag[1]) }}
                </view>
              </view>
              <view class="isDot" v-if='item.isDot'>
                {{ item.isDot }}
              </view>
            </view>
          </template>
          <template v-slot:right="{ rightList }">
            <view class="demo-water" v-for="(item, index) in rightList" :key='index' @click="goDetail(item)">
              <up-lazy-load threshold="-500" border-radius='10' :image="item.image || item.img" :index="index"></up-lazy-load>
              <view class="demo-title">
                {{ item.title }}
              </view>
              <view class="demo-info">
                <text class="price">¥{{ item.price }}</text>
                <text class="count">{{ item.count }}分</text>
                <text class="place">{{ item.place || item.address }}</text>
              </view>
              <view class="demo-price">
                {{ item.times }}
              </view>
              <view class="demo-tag" v-if="(item.tags && item.tags.length) || (item.tag && item.tag.length)">
                <view class="demo-tag-owner" v-if="(item.tags && item.tags[0]) || (item.tag && item.tag[0])">
                  {{ (item.tags && item.tags[0]) || (item.tag && item.tag[0]) }}
                </view>
                <view class="demo-tag-text" v-if="(item.tags && item.tags[1]) || (item.tag && item.tag[1])">
                  {{ (item.tags && item.tags[1]) || (item.tag && item.tag[1]) }}
                </view>
              </view>
              <view class="isDot" v-if='item.isDot'>
                {{ item.isDot }}
              </view>
            </view>
          </template>
        </up-waterfall>
      </template>
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
import { getBanner, getHomeList, getcountList, filter } from '../../api/api.js'
import { onLoad, onReachBottom, onPageScroll } from '@dcloudio/uni-app';
import { ref, computed, watch, nextTick } from 'vue';
import SearchFilter from '../../components/SearchFilter.vue';
import { hasActiveFilters, resetFilters } from '../../utils/filterUtils.js';

const showFilter = ref(false)
const keyword = ref('')
const bannerList = ref([])
const originalList = ref([]) // 原始数据
const currentFilters = ref(resetFilters())
const showTopBtn = ref(0)
const uWaterfallRef = ref(null)
const filterKey = ref(0)
const showWaterfall = ref(true)
const isSortedByRating = ref(false) // 是否按评分排序
const sortedList = ref([]) // 排序后的列表

// 计算筛选后的完整列表
const filteredFullList = computed(() => {
  let filtered = [...originalList.value]
  
  // 只有在没有激活筛选条件时才应用前端关键词搜索
  if (keyword.value.trim() && !hasActiveFiltersComputed.value) {
    const searchTerm = keyword.value.trim()
    filtered = filtered.filter(item => 
      item.title && item.title.includes(searchTerm)
    )
  }
  
  // 如果启用了评分排序，返回排序后的列表
  if (isSortedByRating.value) {
    return sortedList.value.filter(item => 
      filtered.some(originalItem => originalItem.id === item.id)
    )
  }
  
  return filtered
})


// 检查是否有激活的筛选条件
const hasActiveFiltersComputed = computed(() => {
  return hasActiveFilters(currentFilters.value)
})

// 计算左右两列数据（用于排序后的双列布局，按行排列）
const leftColumnList = computed(() => {
  if (!isSortedByRating.value) return []
  const result = []
  for (let i = 0; i < filteredFullList.value.length; i += 2) {
    result.push(filteredFullList.value[i])
  }
  return result
})

const rightColumnList = computed(() => {
  if (!isSortedByRating.value) return []
  const result = []
  for (let i = 1; i < filteredFullList.value.length; i += 2) {
    result.push(filteredFullList.value[i])
  }
  return result
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
  // 转换数据结构以匹配详情页的期望格式
  const transformedItem = {
    img: item.image || item.img, // 将image转换为img
    title: item.title,
    tag: item.tags || item.tag || [], // 将tags转换为tag
    introduce: item.introduction || item.introduce || '', // 将introduction转换为introduce
    times: item.openTime || item.times || '', // 将openTime转换为times
    id: item.id,
    price: item.price,
    count: item.count,
    place: item.place || item.address,
    address: item.address || item.place,
    isRecommended: item.isRecommended,
    status: item.status,
    createTime: item.createTime
  }
  const can = JSON.stringify(transformedItem)
  uni.navigateTo({ url: `/pages/detail/detail?item=${encodeURIComponent(can)}` })
}

const goDetailFromSortedList = (item) => {
  // 转换数据结构以匹配详情页的期望格式
  const transformedItem = {
    img: item.image, // 将image转换为img
    title: item.title,
    tag: item.tags || [], // 将tags转换为tag
    introduce: item.introduction || '', // 将introduction转换为introduce
    times: item.openTime || '', // 将openTime转换为times
    id: item.id,
    price: item.price,
    count: item.count,
    place: item.place,
    address: item.address,
    isRecommended: item.isRecommended,
    status: item.status,
    createTime: item.createTime
  }
  const can = JSON.stringify(transformedItem)
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
const handleFilterChange = async (filters) => {
  // 检查是否有筛选条件
  const hasFilters = Object.values(filters).some(value => 
    value !== '' && value !== null && value !== undefined
  )
  
  if (!hasFilters) {
    // 如果没有筛选条件，直接使用原始数据
    currentFilters.value = resetFilters()
    showFilter.value = false
    return
  }
  
  try {
    // 显示加载状态
    uni.showLoading({
      title: '筛选中...'
    })
    
    // 构建API请求参数，只包含有值的筛选条件
    const filterParams = {}
    if (filters.title && filters.title.trim()) {
      filterParams.title = filters.title.trim()
    }
    if (filters.minPrice) {
      filterParams.minPrice = Number(filters.minPrice)
    }
    if (filters.maxPrice) {
      filterParams.maxPrice = Number(filters.maxPrice)
    }
    if (filters.minCount) {
      filterParams.minCount = Number(filters.minCount)
    }
    if (filters.maxCount) {
      filterParams.maxCount = Number(filters.maxCount)
    }
    if (filters.address && filters.address.trim()) {
      filterParams.address = filters.address.trim()
    }
    
    // 调用后端筛选API
    const res = await filter(filterParams)
    console.log('筛选API返回结果:', res)
    
    // 更新筛选后的数据
    if (Array.isArray(res)) {
      originalList.value = res
    } else if (res.data) {
      originalList.value = res.data
    } else if (res.list) {
      originalList.value = res.list
    } else {
      originalList.value = []
    }
    
    console.log('筛选后数据长度:', originalList.value.length)
    
    // 保存当前筛选条件
    currentFilters.value = { ...filters }
    
    // 关闭筛选面板
    showFilter.value = false
    
    // 使用nextTick确保DOM更新完成
    nextTick(() => {
      uni.hideLoading()
      
      // 强制重新渲染瀑布流组件
      filterKey.value = Date.now()
    })
  } catch (error) {
    console.error('筛选失败:', error)
    uni.hideLoading()
    uni.showToast({
      title: '筛选失败',
      icon: 'error'
    })
  }
}

// 清空所有筛选条件（保持原有逻辑）
const clearAllFilters = async () => {
  try {
    uni.showLoading({
      title: '重置中...'
    })
    
    // 重新获取原始数据
    const res = await getHomeList()
    originalList.value = Array.isArray(res) ? res : (res.data || res.list || [])
    
    currentFilters.value = resetFilters()
    keyword.value = ''
    
    uni.hideLoading()
  } catch (error) {
    console.error('重置失败:', error)
    uni.hideLoading()
    uni.showToast({
      title: '重置失败',
      icon: 'error'
    })
  }
}

// 仅重置筛选条件但不重新获取数据（用于筛选后的重置）
const resetFiltersOnly = () => {
  currentFilters.value = resetFilters()
  keyword.value = ''
  // 强制重新渲染瀑布流组件
  filterKey.value++
}

// 处理搜索输入变化
const handleSearchChange = (value) => {
  keyword.value = value
  console.log('搜索关键词:', keyword.value)
}

  // 监听筛选结果变化
watch(filteredFullList, (newValue) => {
  console.log('filteredFullList长度:', newValue.length)
  console.log('filteredFullList内容:', newValue)
}, { immediate: true })

// 监听originalList变化
watch(originalList, (newValue) => {
  console.log('originalList长度:', newValue.length)
  console.log('originalList内容:', newValue)
}, { immediate: true })

// 切换评分排序
const toggleRatingSort = async () => {
  if (isSortedByRating.value) {
    // 如果已经是排序状态，则关闭排序
    isSortedByRating.value = false
  } else {
    // 如果是未排序状态，则获取排序数据
    try {
      // 显示加载状态
      uni.showLoading({
        title: '加载中...'
      })
      
      const res = await getcountList()
      console.log("获取排序后的数据:", res)
      // 确保获取的是数组数据，正确处理API返回的数据结构
      sortedList.value = Array.isArray(res) ? res : (res.data || res.list || [])
      console.log("处理后的排序数据:", sortedList.value)
      isSortedByRating.value = true
      
      uni.hideLoading()
    } catch (error) {
      console.error('获取排序列表失败:', error)
      uni.hideLoading()
      uni.showToast({
        title: '获取排序数据失败',
        icon: 'error'
      })
    }
  }
  
  // 强制重新渲染列表
  filterKey.value++ // 通过改变key来强制重新渲染
}


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

.sort-button-container {
  margin-bottom: 20rpx;
}

.sort-button {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  border: 2rpx solid #d9d9d9;
  border-radius: 8rpx;
  background-color: #fff;
  font-size: 26rpx;
  color: #666;
  transition: all 0.3s ease;
}

.sort-button-active {
  border-color: #1890ff;
  color: #1890ff;
  background-color: #e6f7ff;
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

/* 双列布局样式 */
.two-column-layout {
  display: flex;
  gap: 20rpx;
}

.two-column-layout .column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.two-column-layout .demo-water {
  margin: 0;
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
