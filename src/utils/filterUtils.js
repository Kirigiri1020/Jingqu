// 重置筛选条件
export const resetFilters = () => {
  return {
    title: '',
    minPrice: '',
    maxPrice: '',
    minCount: '',
    maxCount: '',
    address: ''
  }
}

// 检查是否有筛选条件
export const hasActiveFilters = (filters) => {
  return Object.values(filters).some(value => 
    value !== '' && value !== null && value !== undefined
  )
}
