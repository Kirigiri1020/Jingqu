package com.example.jingqu.service;

import com.example.jingqu.entity.ScenicSpot;
import com.example.jingqu.repository.ScenicSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScenicSpotService {
    
    @Autowired
    private ScenicSpotRepository scenicSpotRepository;
    
    public List<ScenicSpot> getActiveScenicSpots() {
        return scenicSpotRepository.findActiveScenicSpots();
    }
    
    public List<ScenicSpot> getRecommendedScenicSpots() {
        return scenicSpotRepository.findRecommendedScenicSpots();
    }
    
    public List<ScenicSpot> getAllScenicSpots() {
        return scenicSpotRepository.findAll();
    }
    
    public ScenicSpot getScenicSpotById(Long id) {
        return scenicSpotRepository.findById(id).orElse(null);
    }
    
    public ScenicSpot saveScenicSpot(ScenicSpot scenicSpot) {
        return scenicSpotRepository.save(scenicSpot);
    }
    
    public void deleteScenicSpot(Long id) {
        scenicSpotRepository.deleteById(id);
    }
    
    public List<ScenicSpot> getScenicSpotsByStatus(Boolean status) {
        return scenicSpotRepository.findByStatusOrderByCreateTimeDesc(status);
    }
    
    public List<ScenicSpot> getRecommendedScenicSpotsByStatus(Boolean isRecommended, Boolean status) {
        return scenicSpotRepository.findByIsRecommendedAndStatusOrderByCreateTimeDesc(isRecommended, status);
    }
    
    /**
     * 按评分倒序获取所有景区
     */
    public List<ScenicSpot> getScenicSpotsByCountDesc() {
        return scenicSpotRepository.findAllByCountDesc();
    }
    
    /**
     * 获取相似景区推荐
     * @param excludeId 要排除的景区ID
     * @param tags 标签字符串（逗号分隔）
     * @param limit 返回数量限制
     * @return 相似景区列表
     */
    public List<ScenicSpot> getSimilarScenicSpots(Long excludeId, String tags, int limit) {
        if (tags == null || tags.trim().isEmpty()) {
            // 如果没有标签，返回推荐的景区
            return scenicSpotRepository.findRecommendedScenicSpots().stream()
                    .filter(spot -> !spot.getId().equals(excludeId))
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // 匹配所有标签（只要有一个相同就返回）
        // 使用空标签查询，让SQL处理多个标签的匹配
        return scenicSpotRepository.findSimilarScenicSpots(excludeId, tags, limit);
    }
    
    /**
     * 筛选景区
     * 支持按名称、评分范围、价格范围、地理位置筛选
     * @param title 景区名称（模糊匹配）
     * @param minCount 最低评分
     * @param maxCount 最高评分
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param address 地址（模糊匹配）
     * @return 筛选后的景区列表
     */
    public List<ScenicSpot> filterScenicSpots(String title, Double minCount, Double maxCount, 
                                            Double minPrice, Double maxPrice, String address) {
        return scenicSpotRepository.filterScenicSpots(
            title, minCount, maxCount, minPrice, maxPrice, address
        );
    }
}
