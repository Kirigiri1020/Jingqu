<template>
  <view class="project-management">
    <view class="header">
      <text class="title">项目管理</text>
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
            <view class="table-row" v-for="(project, index) in projectList" :key="project.id">
              <view class="table-cell" v-for="column in columns" :key="column.key" :style="{ width: column.width + 'px' }">
                <template v-if="column.key === 'status'">
                  <view 
                    class="switch-container" 
                    @click="project.editing && handleSwitchClick(project, column.key)"
                  >
                    <text>{{ project[column.key] ? '启用' : '禁用' }}</text>
                  </view>
                </template>
                <template v-else>
                  <up-input
                    v-model="project[column.key]"
                    :type="getInputType(column.key)"
                    border="none"
                    :disabled="!project.editing || ['id', 'createtime'].includes(column.key)"
                    @blur="handleInputBlur(project, column.key)"
                  />
                </template>
              </view>
              <view class="table-cell actions" style="width: 200px">
                <up-button 
                  v-if="!project.editing"
                  type="primary" 
                  size="mini" 
                  @click="startEditing(project)"
                >
                  修改数据
                </up-button>
                <up-button 
                  v-else
                  type="success" 
                  size="mini" 
                  @click="confirmSave(project)"
                >
                  保存
                </up-button>
                <up-button 
                  type="error" 
                  size="mini" 
                  @click="confirmDelete(project.id)"
                  :disabled="project.editing"
                >
                  删除
                </up-button>
              </view>
            </view>

            <!-- 新增行 -->
            <view class="table-row new-row" v-if="isAdding">
              <view class="table-cell" v-for="column in columns" :key="column.key" :style="{ width: column.width + 'px' }">
                <template v-if="column.key === 'status'">
                  <up-switch
                    v-model="newProject[column.key]"
                    size="small"
                  />
                </template>
                <template v-else>
                  <up-input
                    v-model="newProject[column.key]"
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
                    新增项目
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
    <view class="empty" v-if="!loading && projectList.length === 0">
      <text>暂无项目数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as api from '../../api/api.js'

interface Project {
  id: number
  title: string
  image: string
  scenicspotid: number
  description: string
  tag: string
  latitude: number
  longitude: number
  status: boolean
  createtime: string
  editing?: boolean
  originalData?: any
  activeField?: string
}

const loading = ref(false)
const projectList = ref<Project[]>([])
const isAdding = ref(false)
const newProject = ref<Partial<Project>>({})

const columns = [
  { key: 'id', title: 'ID', width: 80 },
  { key: 'title', title: '项目名称', width: 150 },
  { key: 'image', title: '图片', width: 200 },
  { key: 'scenicspotid', title: '景区ID', width: 100 },
  { key: 'description', title: '描述', width: 250 },
  { key: 'tag', title: '标签', width: 120 },
  { key: 'latitude', title: '纬度', width: 120 },
  { key: 'longitude', title: '经度', width: 120 },
  { key: 'status', title: '状态', width: 100 },
  { key: 'createtime', title: '创建时间', width: 180 }
]

const getInputType = (key: string) => {
  const typeMap: { [key: string]: string } = {
    id: 'number',
    scenicspotid: 'number',
    latitude: 'number',
    longitude: 'number',
    status: 'text'
  }
  return typeMap[key] || 'text'
}

// 状态选项
const statusOptions = [
  { label: '启用', value: true },
  { label: '禁用', value: false }
]

// 获取项目数据
const fetchProject = async () => {
  loading.value = true
  try {
    const response = await api.getallprojectinfo()
    projectList.value = (response.projects || []).map(project => ({
      ...project,
      editing: false,
      originalData: null
    }))
  } catch (error) {
    console.error('获取项目数据失败:', error)
    uni.showToast({
      title: '获取项目数据失败',
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}

// 开始编辑
const startEditing = (project: Project) => {
  projectList.value.forEach(p => {
    if (p.id !== project.id && p.editing) {
      cancelEditing(p)
    }
  })
  
  project.editing = true
  project.originalData = { ...project }
}

// 取消编辑
const cancelEditing = (project: Project) => {
  if (project.originalData) {
    Object.assign(project, project.originalData)
  }
  project.editing = false
  project.originalData = null
}

// 输入框失去焦点处理
const handleInputBlur = (project: Project, key: string) => {
  // 这里可以添加一些验证逻辑
}

// 处理开关点击
const handleSwitchClick = (project: Project, key: string) => {
  if (project.editing) {
    // 切换状态值
    project[key] = !project[key]
  }
}

// 确认保存
const confirmSave = (project: Project) => {
  uni.showModal({
    title: '确认',
    content: '是否确定更改数据？',
    success: (res) => {
      if (res.confirm) {
        saveProject(project)
      }
    }
  })
}

// 保存项目修改
const saveProject = async (project: Project) => {
  // 保存当前滚动位置
  return new Promise((resolve) => {
    const query = uni.createSelectorQuery()
    query.select('.table-scroll').scrollOffset()
    query.exec((res) => {
      const scrollTop = res && res[0] ? res[0].scrollTop : 0
      performSaveOperation(project, scrollTop).then(resolve)
    })
  })
}

// 执行保存操作
const performSaveOperation = async (project: Project, scrollTop: number) => {
  try {
    // 构建要提交的数据对象，排除编辑状态相关字段
    const projectData = {
      id: project.id,
      title: project.title,
      image: project.image,
      scenicspotid: project.scenicspotid,
      description: project.description,
      tag: project.tag,
      latitude: project.latitude,
      longitude: project.longitude,
      status: project.status
    }
    
    console.log('准备发送的项目数据:', projectData)
    
    await api.updatejprojectinfo(projectData)
    project.editing = false
    project.originalData = null
    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })
    // 重新加载数据以确保数据最新
    await fetchProject()
    
    // 恢复滚动位置 - 微信小程序兼容方法
    setTimeout(() => {
      const query = uni.createSelectorQuery()
      query.select('.table-scroll').boundingClientRect()
      query.selectViewport().scrollOffset()
      query.exec((res) => {
        if (res[0] && scrollTop > 0) {
          // 使用scroll-view的scroll-top属性
          uni.pageScrollTo({
            scrollTop: scrollTop,
            duration: 0
          })
        }
      })
    }, 100)
  } catch (error) {
    console.error('保存项目信息失败:', error)
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
const confirmDelete = (projectId: number) => {
  uni.showModal({
    title: '确认删除',
    content: '是否确定删除该项目？',
    success: (res) => {
      if (res.confirm) {
        deleteProject(projectId)
      }
    }
  })
}

// 删除项目
const deleteProject = async (projectId: number) => {
  // 保存当前滚动位置
  return new Promise((resolve) => {
    const query = uni.createSelectorQuery()
    query.select('.table-scroll').scrollOffset()
    query.exec((res) => {
      const scrollTop = res && res[0] ? res[0].scrollTop : 0
      performDeleteOperation(projectId, scrollTop).then(resolve)
    })
  })
}

// 执行删除操作
const performDeleteOperation = async (projectId: number, scrollTop: number) => {
  try {
    await api.deleteprojectinfo(projectId)
    uni.showToast({
      title: '删除成功',
      icon: 'success'
    })
    // 重新加载数据
    await fetchProject()
    
    // 恢复滚动位置 - 微信小程序兼容方法
    setTimeout(() => {
      const query = uni.createSelectorQuery()
      query.select('.table-scroll').boundingClientRect()
      query.selectViewport().scrollOffset()
      query.exec((res) => {
        if (res[0] && scrollTop > 0) {
          // 使用uni.pageScrollTo方法
          uni.pageScrollTo({
            scrollTop: scrollTop,
            duration: 0
          })
        }
      })
    }, 100)
  } catch (error) {
    console.error('删除项目失败:', error)
    uni.showToast({
      title: '删除失败',
      icon: 'error'
    })
  }
}

// 开始新增
const startAdding = () => {
  isAdding.value = true
  // 初始化新项目数据
  newProject.value = {
    title: '',
    image: '',
    scenicspotid: 0,
    description: '',
    tag: '',
    latitude: 0,
    longitude: 0,
    status: true
  }
}

// 确认新增
const confirmAdd = () => {
  // 验证必填字段
  if (!newProject.value.title || !newProject.value.image) {
    uni.showToast({
      title: '项目名称和图片URL为必填项',
      icon: 'error',
      duration: 3000
    })
    return
  }

  uni.showModal({
    title: '确认',
    content: '是否确定新增项目？',
    success: (res) => {
      if (res.confirm) {
        addProject()
      }
    }
  })
}

// 取消新增
const cancelAdding = () => {
  isAdding.value = false
  newProject.value = {}
}

// 新增项目
const addProject = async () => {
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
    const projectData = {
      title: newProject.value.title || '',
      image: newProject.value.image || '',
      scenicspotid: newProject.value.scenicspotid || 0,
      description: newProject.value.description || '',
      tag: newProject.value.tag || '',
      latitude: newProject.value.latitude || 0,
      longitude: newProject.value.longitude || 0,
      status: newProject.value.status !== undefined ? newProject.value.status : true
    }
    
    console.log('准备新增的项目数据:', projectData)
    
    await api.addprojectinfo(projectData)
    isAdding.value = false
    newProject.value = {}
    uni.showToast({
      title: '新增成功',
      icon: 'success'
    })
    // 重新加载数据
    await fetchProject()
    
    // 恢复滚动位置 - 微信小程序兼容方法
    setTimeout(() => {
      const query = uni.createSelectorQuery()
      query.select('.table-scroll').boundingClientRect()
      query.selectViewport().scrollOffset()
      query.exec((res) => {
        if (res[0] && scrollTop > 0) {
          // 使用uni.pageScrollTo方法
          uni.pageScrollTo({
            scrollTop: scrollTop,
            duration: 0
          })
        }
      })
    }, 100)
  } catch (error) {
    console.error('新增项目失败:', error)
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
  fetchProject()
})
</script>

<style lang="scss" scoped>
.project-management {
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
