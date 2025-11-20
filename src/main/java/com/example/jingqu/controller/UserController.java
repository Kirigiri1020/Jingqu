package com.example.jingqu.controller;

import com.example.jingqu.common.ApiResponse;
import com.example.jingqu.entity.Banner;
import com.example.jingqu.entity.ScenicSpot;
import com.example.jingqu.service.BannerService;
import com.example.jingqu.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    
    @Autowired
    private BannerService bannerService;
    
    @Autowired
    private ScenicSpotService scenicSpotService;
    
    @GetMapping("/user/getBanner")
    public ApiResponse<Map<String, Object>> getBanner() {
        try {
            List<Banner> banners = bannerService.getActiveBanners();
            List<Map<String, String>> bannerList = new ArrayList<>();
            
            for (Banner banner : banners) {
                Map<String, String> bannerMap = new HashMap<>();
                bannerMap.put("image", banner.getImage());
                bannerMap.put("title", banner.getTitle());
                bannerList.add(bannerMap);
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("bannerList", bannerList);
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/getHomeList")
    public ApiResponse<List<Map<String, Object>>> getHomeList() {
        try {
            List<ScenicSpot> scenicSpots = scenicSpotService.getActiveScenicSpots();
            List<Map<String, Object>> homeList = new ArrayList<>();
            
            for (ScenicSpot spot : scenicSpots) {
                Map<String, Object> spotMap = new HashMap<>();
                spotMap.put("id", spot.getId());
                spotMap.put("title", spot.getTitle());
                spotMap.put("img", spot.getImage());
                
                // 处理标签数组
                String[] tags = spot.getTags() != null ? spot.getTags().split(",") : new String[0];
                spotMap.put("tag", tags);
                
                spotMap.put("isDot", spot.getIsRecommended() ? "推荐" : "");
                spotMap.put("dot", spot.getIsRecommended());
                spotMap.put("introduce", spot.getIntroduction());
                spotMap.put("times", spot.getOpenTime());
                spotMap.put("isPlay", false); // 默认未游玩

                // 处理地址坐标 - 使用默认坐标，因为经纬度字段已移除
                List<String> address = new ArrayList<>();
                address.add("116.410886");
                address.add("39.881949");
                spotMap.put("address", address);

                // 添加place、count和price字段
                spotMap.put("place", spot.getPlace() != null ? spot.getPlace() : "");
                spotMap.put("count", spot.getCount() != null ? spot.getCount() : 0.0);
                spotMap.put("price", spot.getPrice() != null ? spot.getPrice() : 0.0);

                homeList.add(spotMap);
            }
            
            return ApiResponse.success(homeList);
            
        } catch (Exception e) {
            return ApiResponse.error("获取首页列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取相似景区推荐
     * 接口路径: POST /jingqu/similar
     * 请求参数: {"id": 景区ID}
     */
    @PostMapping("/jingqu/similar")
    public ApiResponse<List<Map<String, Object>>> getSimilarScenicSpots(@RequestBody Map<String, Object> request) {
        try {
            // 从请求体中获取景区ID
            Object idObj = request.get("id");
            if (idObj == null) {
                return ApiResponse.error("景区ID不能为空");
            }
            
            Long id;
            if (idObj instanceof Number) {
                id = ((Number) idObj).longValue();
            } else if (idObj instanceof String) {
                try {
                    id = Long.parseLong((String) idObj);
                } catch (NumberFormatException e) {
                    return ApiResponse.error("景区ID格式不正确");
                }
            } else {
                return ApiResponse.error("景区ID格式不正确");
            }
            
            // 获取当前景区信息
            ScenicSpot currentSpot = scenicSpotService.getScenicSpotById(id);
            if (currentSpot == null) {
                return ApiResponse.error("景区不存在");
            }
            
            // 获取相似景区推荐（返回所有相似景区）
            List<ScenicSpot> similarSpots = scenicSpotService.getSimilarScenicSpots(
                id, currentSpot.getTags(), Integer.MAX_VALUE);
            
            // 转换景区信息格式（使用小写字段名）
            List<Map<String, Object>> similarSpotList = new ArrayList<>();
            for (ScenicSpot spot : similarSpots) {
                Map<String, Object> spotMap = new HashMap<>();
                spotMap.put("id", spot.getId());
                spotMap.put("title", spot.getTitle());
                spotMap.put("image", spot.getImage());
                
                // 处理标签数组
                String[] tags = spot.getTags() != null ? spot.getTags().split(",") : new String[0];
                spotMap.put("tags", tags);
                
                spotMap.put("isrecommended", spot.getIsRecommended());
                spotMap.put("introduction", spot.getIntroduction());
                spotMap.put("opentime", spot.getOpenTime());
                
                // 添加地址信息
                if (spot.getAddress() != null) {
                    spotMap.put("address", spot.getAddress());
                }
                
                // 添加place、count和price字段
                spotMap.put("place", spot.getPlace() != null ? spot.getPlace() : "");
                spotMap.put("count", spot.getCount() != null ? spot.getCount() : 0.0);
                spotMap.put("price", spot.getPrice() != null ? spot.getPrice() : 0.0);
                
                // 经纬度字段已从实体类中移除，不再返回
                
                spotMap.put("status", spot.getStatus());
                spotMap.put("createtime", spot.getCreateTime());
                
                similarSpotList.add(spotMap);
            }
            
            return ApiResponse.success(similarSpotList);
            
        } catch (Exception e) {
            return ApiResponse.error("获取相似景区推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取按评分排序的景区列表
     * 接口路径: GET /api/user/getcountList
     */
    @GetMapping("/user/getcountList")
    public ApiResponse<List<Map<String, Object>>> getCountList() {
        try {
            List<ScenicSpot> scenicSpots = scenicSpotService.getScenicSpotsByCountDesc();
            List<Map<String, Object>> countList = new ArrayList<>();
            
            for (ScenicSpot spot : scenicSpots) {
                Map<String, Object> spotMap = new HashMap<>();
                spotMap.put("id", spot.getId());
                spotMap.put("title", spot.getTitle());
                spotMap.put("image", spot.getImage());
                
                // 处理标签数组
                String[] tags = spot.getTags() != null ? spot.getTags().split(",") : new String[0];
                spotMap.put("tags", tags);
                
                spotMap.put("isRecommended", spot.getIsRecommended());
                spotMap.put("introduction", spot.getIntroduction());
                spotMap.put("openTime", spot.getOpenTime());
                
                // 添加地址信息
                if (spot.getAddress() != null) {
                    spotMap.put("address", spot.getAddress());
                }
                
                // 添加place、count和price字段
                spotMap.put("place", spot.getPlace() != null ? spot.getPlace() : "");
                spotMap.put("count", spot.getCount() != null ? spot.getCount() : 0.0);
                spotMap.put("price", spot.getPrice() != null ? spot.getPrice() : 0.0);
                
                spotMap.put("status", spot.getStatus());
                spotMap.put("createTime", spot.getCreateTime());
                
                countList.add(spotMap);
            }
            
            return ApiResponse.success(countList);
            
        } catch (Exception e) {
            return ApiResponse.error("获取评分排序列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 筛选景区
     * 接口路径: POST /api/user/filter
     * 支持按名称、评分范围、价格范围、地理位置筛选
     */
    @PostMapping("/user/filter")
    public ApiResponse<List<Map<String, Object>>> filterScenicSpots(@RequestBody Map<String, Object> filterParams) {
        try {
            // 解析筛选参数
            String title = (String) filterParams.get("title");
            Double minCount = filterParams.get("minCount") != null ? 
                Double.valueOf(filterParams.get("minCount").toString()) : null;
            Double maxCount = filterParams.get("maxCount") != null ? 
                Double.valueOf(filterParams.get("maxCount").toString()) : null;
            Double minPrice = filterParams.get("minPrice") != null ? 
                Double.valueOf(filterParams.get("minPrice").toString()) : null;
            Double maxPrice = filterParams.get("maxPrice") != null ? 
                Double.valueOf(filterParams.get("maxPrice").toString()) : null;
            String address = (String) filterParams.get("address");
            
            // 执行筛选
            List<ScenicSpot> filteredSpots = scenicSpotService.filterScenicSpots(
                title, minCount, maxCount, minPrice, maxPrice, address
            );
            
            // 转换景区信息格式
            List<Map<String, Object>> filteredList = new ArrayList<>();
            
            for (ScenicSpot spot : filteredSpots) {
                Map<String, Object> spotMap = new HashMap<>();
                spotMap.put("id", spot.getId());
                spotMap.put("title", spot.getTitle());
                spotMap.put("image", spot.getImage());
                
                // 处理标签数组
                String[] tags = spot.getTags() != null ? spot.getTags().split(",") : new String[0];
                spotMap.put("tags", tags);
                
                spotMap.put("isRecommended", spot.getIsRecommended());
                spotMap.put("introduction", spot.getIntroduction());
                spotMap.put("openTime", spot.getOpenTime());
                
                // 添加地址信息
                if (spot.getAddress() != null) {
                    spotMap.put("address", spot.getAddress());
                }
                
                // 添加place、count和price字段
                spotMap.put("place", spot.getPlace() != null ? spot.getPlace() : "");
                spotMap.put("count", spot.getCount() != null ? spot.getCount() : 0.0);
                spotMap.put("price", spot.getPrice() != null ? spot.getPrice() : 0.0);
                
                spotMap.put("status", spot.getStatus());
                spotMap.put("createTime", spot.getCreateTime());
                
                filteredList.add(spotMap);
            }
            
            return ApiResponse.success(filteredList);
            
        } catch (Exception e) {
            return ApiResponse.error("筛选景区失败: " + e.getMessage());
        }
    }
}
