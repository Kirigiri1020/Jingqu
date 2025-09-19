<template>
  <view class="jingqu-management">
  <view class="header">
      <text class="title">景区管理</text>
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
            <view class="table-row" v-for="(jingqu, index) in jingquList" :key="jingqu.id">
              <view class="table-cell" v-for="column in columns" :key="column.key" :style="{ width: column.width + 'px' }">
                <template v-if="column.key === 'status' || column.key === 'isrecommended'">
                  <view 
                    class="switch-container" 
                    @click="jingqu.editing && handleSwitchClick(jingqu, column.key)"
                  >
                    <text>{{ jingqu[column.key] ? (column.key === 'status' ? '启用' : '是') : (column.key === 'status' ? '禁用' : '否') }}</text>
                  </view>
                </template>
                <template v-else>
                  <up-input
                    v-model="jingqu[column.key]"
                    :type="getInputType(column.key)"
                    border="none"
                    :disabled="!jingqu.editing || ['id', 'createtime'].includes(column.key)"
                    @blur="handleInputBlur(jingqu, column.key)"
                  />
                </template>
              </view>
              <view class="table-cell actions" style="width: 200px">
                <up-button 
                  v-if="!jingqu.editing"
                  type="primary" 
                  size="mini" 
                  @click="startEditing(jingqu)"
                >
                  修改数据
                </up-button>
                <up-button 
                  v-else
                  type="success" 
                  size="mini" 
                  @click="confirmSave(jingqu)"
                >
                  保存
                </up-button>
                <up-button 
                  type="error" 
                  size="mini" 
                  @click="confirmDelete(jingqu.id)"
                  :disabled="jingqu.editing"
                >
                  删除
                </up-button>
              </view>
            </view>

            <!-- 新增行 -->
            <view class="table-row new-row" v-if="isAdding">
              <view class="table-cell" v-for="column in columns" :key="column.key" :style="{ width: column.width + 'px' }">
                <template v-if="column.key === 'status' || column.key === 'isrecommended'">
                  <up-switch
                    v-model="newJingqu[column.key]"
                    size="small"
                  />
                </template>
                <template v-else>
                  <up-input
                    v-model="newJingqu[column.key]"
                    :type="getInputType(column.key)"
                    border="none"
                    :disabled="['id', 'createtime'].includes(column.key)"
                    :placeholder="column.key === 'id' || column.key === 'createtime' ? '自动生成' : `请输入${column.title}`"
                  />
                </template>
              </view>
              <view class="table-cell actions" style="width: 200px">
                <up-button 
                  type="warning" 
                  size="mini" 
                  @click="cancelAdding"
                >
                  取消
                </up-button>
              </view>
            </view>

            <!-- 新增按钮 -->
            <view class="table-row add-button-row">
              <view class="table-cell" :style="{ width: '100%' }">
                <view class="add-button-container">
                  <up-button 
                    v-if="!isAdding"
                    type="primary" 
                    @click="startAdding"
                  >
                    新增景区
                  </up-button>
                  <up-button 
                    v-else
                    type="success" 
                    @click="confirmAdd"
                  >
                    保存新增
                  </up-button>
                </view>
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
    <view class="empty" v-if="!loading && jingquList.length === 0">
      <text>暂无景区数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getalljingquinfo, updatejingquinfo, deletejingquinfo, addjingquinfo } from '../../api/api.js'

interface Jingqu {
  id: number
  title: string
  image: string
  address: string
  place: string
  count: number
  opentime: string
  isrecommended: boolean
  tags: string
  price: number
  introduction: string
  status: boolean
  createtime: string
  editing?: boolean
  originalData?: any
  activeField?: string
}

const loading = ref(false)
const jingquList = ref<Jingqu[]>([])
const isAdding = ref(false)
const newJingqu = ref<Partial<Jingqu>>({})

const columns = [
  { key: 'id', title: 'ID', width: 80 },
  { key: 'title', title: '景区名称', width: 150 },
  { key: 'image', title: '图片', width: 200 },
  { key: 'address', title: '详细地址', width: 200 },
  { key: 'place', title: '地区', width: 120 },
  { key: 'count', title: '评分', width: 100 },
  { key: 'opentime', title: '开放时间', width: 150 },
  { key: 'isrecommended', title: '是否推荐', width: 100 },
  { key: 'tags', title: '标签', width: 150 },
  { key: 'price', title: '价格', width: 100 },
  { key: 'introduction', title: '介绍', width: 200 },
  { key: 'status', title: '状态', width: 100 },
  { key: 'createtime', title: '创建时间', width: 180 }
]

const getInputType = (key: string) => {
  const typeMap: { [key: string]: string } = {
    id: 'number',
    count: 'number',
    price: 'number',
    isrecommended: 'text',
    status: 'text'
  }
  return typeMap[key] || 'text'
}

// 状态选项
const statusOptions = [
  { label: '启用', value: true },
  { label: '禁用', value: false }
]

// 是否推荐选项
const recommendedOptions = [
  { label: '是', value: true },
  { label: '否', value: false }
]

// 获取景区数据
const fetchJingqu = async () => {
  loading.value = true
  try {
    const response = await getalljingquinfo()
    jingquList.value = (response.jingqu || []).map(jingqu => ({
      ...jingqu,
      editing: false,
      originalData: null
    }))
  } catch (error) {
    console.error('获取景区数据失败:', error)
    uni.showToast({
      title: '获取景区数据失败',
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}

// 开始编辑
const startEditing = (jingqu: Jingqu) => {
  jingquList.value.forEach(j => {
    if (j.id !== jingqu.id && j.editing) {
      cancelEditing(j)
    }
  })
  
  jingqu.editing = true
  jingqu.originalData = { ...jingqu }
}

// 取消编辑
const cancelEditing = (jingqu: Jingqu) => {
  if (jingqu.originalData) {
    Object.assign(jingqu, jingqu.originalData)
  }
  jingqu.editing = false
  jingqu.originalData = null
}

// 输入框失去焦点处理
const handleInputBlur = (jingqu: Jingqu, key: string) => {
  // 这里可以添加一些验证逻辑
}

// 处理开关点击
const handleSwitchClick = (jingqu: Jingqu, key: string) => {
  if (jingqu.editing) {
    // 切换状态值
    jingqu[key] = !jingqu[key]
  }
}

// 确认保存
const confirmSave = (jingqu: Jingqu) => {
  uni.showModal({
    title: '确认',
    content: '是否确定更改数据？',
    success: (res) => {
      if (res.confirm) {
        saveJingqu(jingqu)
      }
    }
  })
}

// 保存景区修改
const saveJingqu = async (jingqu: Jingqu) => {
  // 保存当前滚动位置
  return new Promise((resolve) => {
    const query = uni.createSelectorQuery()
    query.select('.table-scroll').scrollOffset()
    query.exec((res) => {
      const scrollTop = res && res[0] ? res[0].scrollTop : 0
      performSaveOperation(jingqu, scrollTop).then(resolve)
    })
  })
}

// 执行保存操作
const performSaveOperation = async (jingqu: Jingqu, scrollTop: number) => {
  try {
    // 构建要提交的数据对象，排除编辑状态相关字段
    const jingquData = {
      id: jingqu.id,
      title: jingqu.title,
      image: jingqu.image,
      address: jingqu.address,
      place: jingqu.place,
      count: jingqu.count,
      opentime: jingqu.opentime,
      isrecommended: jingqu.isrecommended,
      tags: jingqu.tags,
      price: jingqu.price,
      introduction: jingqu.introduction,
      status: jingqu.status
    }
    
    console.log('准备发送的景区数据:', jingquData)
    
    await updatejingquinfo(jingquData)
    jingqu.editing = false
    jingqu.originalData = null
    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })
    // 重新加载数据以确保数据最新
    await fetchJingqu()
    
    // 恢复滚动位置
    setTimeout(() => {
      const query = uni.createSelectorQuery()
      query.select('.table-scroll').node((res) => {
        if (res && res.node && scrollTop > 0) {
          // 使用scroll-view的原生滚动方法
          res.node.scrollTo({
            top: scrollTop,
            animated: false
          })
        }
      }).exec()
    }, 100)
  } catch (error) {
    console.error('保存景区信息失败:', error)
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
const confirmDelete = (jingquId: number) => {
  uni.showModal({
    title: '确认删除',
    content: '是否确定删除该景区？',
    success: (res) => {
      if (res.confirm) {
        deleteJingqu(jingquId)
      }
    }
  })
}

// 删除景区
const deleteJingqu = async (jingquId: number) => {
  // 保存当前滚动位置
  return new Promise((resolve) => {
    const query = uni.createSelectorQuery()
    query.select('.table-scroll').scrollOffset()
    query.exec((res) => {
      const scrollTop = res && res[0] ? res[0].scrollTop : 0
      performDeleteOperation(jingquId, scrollTop).then(resolve)
    })
  })
}

// 执行删除操作
const performDeleteOperation = async (jingquId: number, scrollTop: number) => {
  try {
    await deletejingquinfo(jingquId)
    uni.showToast({
      title: '删除成功',
      icon: 'success'
    })
    // 重新加载数据
    await fetchJingqu()
    
    // 恢复滚动位置
    setTimeout(() => {
      const query = uni.createSelectorQuery()
      query.select('.table-scroll').node((res) => {
        if (res && res.node && scrollTop > 0) {
          // 使用scroll-view的原生滚动方法
          res.node.scrollTo({
            top: scrollTop,
            animated: false
          })
        }
      }).exec()
    }, 100)
  } catch (error) {
    console.error('删除景区失败:', error)
    uni.showToast({
      title: '删除失败',
      icon: 'error'
    })
  }
}

// 开始新增
const startAdding = () => {
  isAdding.value = true
  // 初始化新景区数据
  newJingqu.value = {
    title: '',
    image: '',
    address: '',
    place: '',
    count: 0,
    opentime: '',
    isrecommended: false,
    tags: '',
    price: 0,
    introduction: '',
    status: true
  }
}

// 确认新增
const confirmAdd = () => {
  // 验证必填字段
  if (!newJingqu.value.title || !newJingqu.value.image) {
    uni.showToast({
      title: '景区名称和图片URL为必填项',
      icon: 'error',
      duration: 3000
    })
    return
  }

  uni.showModal({
    title: '确认',
    content: '是否确定新增景区？',
    success: (res) => {
      if (res.confirm) {
        addJingqu()
      }
    }
  })
}

// 取消新增
const cancelAdding = () => {
  isAdding.value = false
  newJingqu.value = {}
}

// 新增景区
const addJingqu = async () => {
  // 保存当前滚动位置
  return new Promise((resolve) => {
    const query = uni.createSelectorQuery()
    query.select('.table-scroll').scrollOffset()
    query.exec((res) => {
      const scrollTop = res && res[0] ? res[0].scrollTop : 0
      performAddOperation(scrollTop).then(resolve)
    })
  })
}

// 执行新增操作
const performAddOperation = async (scrollTop: number) => {
  try {
    // 构建要提交的数据对象
    const jingquData = {
      title: newJingqu.value.title || '',
      image: newJingqu.value.image || '',
      address: newJingqu.value.address || '',
      place: newJingqu.value.place || '',
      count: newJingqu.value.count || 0,
      opentime: newJingqu.value.opentime || '',
      isrecommended: newJingqu.value.isrecommended || false,
      tags: newJingqu.value.tags || '',
      price: newJingqu.value.price || 0,
      introduction: newJingqu.value.introduction || '',
      status: newJingqu.value.status !== undefined ? newJingqu.value.status : true
    }
    
    console.log('准备新增的景区数据:', jingquData)
    
    await addjingquinfo(jingquData)
    isAdding.value = false
    newJingqu.value = {}
    uni.showToast({
      title: '新增成功',
      icon: 'success'
    })
    // 重新加载数据
    await fetchJingqu()
    
    // 恢复滚动位置
    setTimeout(() => {
      const query = uni.createSelectorQuery()
      query.select('.table-scroll').node((res) => {
        if (res && res.node && scrollTop > 0) {
          // 使用scroll-view的原生滚动方法
          res.node.scrollTo({
            top: scrollTop,
            animated: false
          })
        }
      }).exec()
    }, 100)
  } catch (error) {
    console.error('新增景区失败:', error)
    console.error('错误详情:', error)
    // 显示具体的错误信息
    uni.showToast({
      title: error.message || '新增失败',
      icon: 'error',
      duration: 3000
    })
  }
}

onMounted(() => {
  fetchJingqu()
})
</script>

<style lang="scss" scoped>
.jingqu-management {
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
  min-width: 2000px; // 增加宽度以适应更多列
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.table-cell {
  padding: 15rpx;
  display: flex;
  align-items: center;
  justify-content: center;
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

/* 新增行样式 */
.new-row {
  background-color: #f0f9ff;
  border: 1px dashed #007bff;
}

/* 新增按钮行样式 */
.add-button-row {
  border: none !important;
  background-color: transparent !important;
}

.add-button-row .table-cell {
  justify-content: flex-start;
  padding: 20rpx;
}

.add-button-container {
  text-align: left;
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

// 选择容器样式
.select-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  position: relative;
  z-index: 10;
}

// 开关容器样式
.switch-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

// 确保选择框有足够的层级
:deep(.u-select) {
  z-index: 1000 !important;
}

:deep(.u-select__content) {
  z-index: 1001 !important;
}
</style>
