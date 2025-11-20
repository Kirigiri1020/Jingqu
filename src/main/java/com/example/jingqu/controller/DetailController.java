package com.example.jingqu.controller;

import com.example.jingqu.common.ApiResponse;
import com.example.jingqu.entity.Project;
import com.example.jingqu.entity.ScenicSpot;
import com.example.jingqu.service.ProjectService;
import com.example.jingqu.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detail")
public class DetailController {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ScenicSpotService scenicSpotService;
    
    @GetMapping("/project")
    public ApiResponse<List<Map<String, Object>>> getProjects(@RequestParam(required = false) Long scenicSpotId) {
        try {
            List<Project> projects;
            if (scenicSpotId != null) {
                projects = projectService.getActiveProjectsByScenicSpotId(scenicSpotId);
            } else {
                projects = projectService.getActiveProjects();
            }
            
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            for (Project project : projects) {
                Map<String, Object> projectMap = new HashMap<>();
                projectMap.put("id", project.getId());
                projectMap.put("belong", project.getScenicSpotId()); // 添加belong字段表示所属景区
                projectMap.put("title", project.getTitle());
                projectMap.put("url", project.getImage());
                projectMap.put("tag", project.getTag());
                projectMap.put("desc", project.getDescription());
                
                // 添加经纬度信息
                if (project.getLatitude() != null && project.getLongitude() != null) {
                    projectMap.put("latitude", project.getLatitude());
                    projectMap.put("longitude", project.getLongitude());
                }
                
                projectList.add(projectMap);
            }
            
            return ApiResponse.success(projectList);
            
        } catch (Exception e) {
            return ApiResponse.error("获取游玩项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取相似景区推荐
     * 接口路径: GET /api/detail/similar-scenic-spots
     * 请求参数: id=景区ID
     */
    @GetMapping("/similar-scenic-spots")
    public ApiResponse<List<Map<String, Object>>> getSimilarScenicSpots(@RequestParam Long id) {
        try {
            // 获取当前景区信息
            ScenicSpot currentSpot = scenicSpotService.getScenicSpotById(id);
            if (currentSpot == null) {
                return ApiResponse.error("景区不存在");
            }
            
            // 获取相似景区推荐（最多推荐4个）
            List<ScenicSpot> similarSpots = scenicSpotService.getSimilarScenicSpots(
                id, currentSpot.getTags(), Integer.MAX_VALUE);
            
            // 转换景区信息格式
            List<Map<String, Object>> similarSpotList = new ArrayList<>();
            for (ScenicSpot spot : similarSpots) {
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
                
                // 经纬度字段已从实体类中移除，不再返回
                
                similarSpotList.add(spotMap);
            }
            
            return ApiResponse.success(similarSpotList);
            
        } catch (Exception e) {
            return ApiResponse.error("获取相似景区推荐失败: " + e.getMessage());
        }
    }
}
