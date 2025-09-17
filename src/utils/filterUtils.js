// 筛选工具函数
export const filterScenicSpots = (items, filters) => {
  if (!items || !Array.isArray(items)) return []
  
  return items.filter(item => {
    // 景区名称筛选
    if (filters.title && filters.title.trim()) {
      const title = item.title || ''
      if (!title.toLowerCase().includes(filters.title.toLowerCase().trim())) {
        return false
      }
    }
    
    // 价格范围筛选
    if (filters.minPrice || filters.maxPrice) {
      const price = item.price || 0
      const minPrice = filters.minPrice ? Number(filters.minPrice) : 0
      const maxPrice = filters.maxPrice ? Number(filters.maxPrice) : Infinity
      
      if (price < minPrice || price > maxPrice) {
        return false
      }
    }
    
    // 评分范围筛选
    if (filters.minCount || filters.maxCount) {
      const count = item.count || 0
      const minCount = filters.minCount ? Number(filters.minCount) : 0
      const maxCount = filters.maxCount ? Number(filters.maxCount) : 100
      
      if (count < minCount || count > maxCount) {
        return false
      }
    }
    
    // 地理位置筛选
    if (filters.place && filters.place.trim()) {
      const place = item.place || ''
      if (!place.includes(filters.place.trim())) {
        return false
      }
    }
    
    return true
  })
}

// 重置筛选条件
export const resetFilters = () => {
  return {
    title: '',
    minPrice: '',
    maxPrice: '',
    minCount: '',
    maxCount: '',
    place: ''
  }
}

// 检查是否有筛选条件
export const hasActiveFilters = (filters) => {
  return Object.values(filters).some(value => 
    value !== '' && value !== null && value !== undefined
  )
}
