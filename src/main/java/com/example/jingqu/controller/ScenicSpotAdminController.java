package com.example.jingqu.controller;

import com.example.jingqu.common.ApiResponse;
import com.example.jingqu.entity.ScenicSpot;
import com.example.jingqu.service.ScenicSpotService;
import com.example.jingqu.service.UserService;
import com.example.jingqu.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class ScenicSpotAdminController {
    
    @Autowired
    private ScenicSpotService scenicSpotService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 创建景区信息（管理员功能）
     * 接口路径: POST /admin/jingqu/add
     * 请求头: token: jwt_token_string
     */
    @PostMapping("/jingqu/add")
    public ApiResponse<Map<String, Object>> createScenicSpot(@RequestHeader("token") String token,
                                                           @RequestBody Map<String, Object> scenicSpotRequest) {
        try {
            // 验证token有效性
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("Token无效");
            }
            
            // 检查管理员权限
            Long adminUserId = jwtUtil.getUserIdFromToken(token);
            if (!userService.isAdmin(adminUserId)) {
                return ApiResponse.unauthorized("无管理员权限");
            }
            
            // 验证必填字段（全部使用小写字段名）
            String title = (String) scenicSpotRequest.get("title");
            String image = (String) scenicSpotRequest.get("image");
            
            if (title == null || title.isEmpty()) {
                return ApiResponse.error("景区名称不能为空");
            }
            
            if (image == null || image.isEmpty()) {
                return ApiResponse.error("景区图片不能为空");
            }
            
            // 创建景区对象
            ScenicSpot scenicSpot = new ScenicSpot();
            scenicSpot.setTitle(title);
            scenicSpot.setImage(image);
            
            // 设置可选字段（全部使用小写字段名）
            if (scenicSpotRequest.containsKey("tags")) {
                scenicSpot.setTags((String) scenicSpotRequest.get("tags"));
            }
            
            if (scenicSpotRequest.containsKey("isrecommended")) {
                Boolean isRecommended = (Boolean) scenicSpotRequest.get("isrecommended");
                scenicSpot.setIsRecommended(isRecommended != null ? isRecommended : false);
            }
            
            if (scenicSpotRequest.containsKey("introduction")) {
                scenicSpot.setIntroduction((String) scenicSpotRequest.get("introduction"));
            }
            
            if (scenicSpotRequest.containsKey("opentime")) {
                scenicSpot.setOpenTime((String) scenicSpotRequest.get("opentime"));
            }
            
            if (scenicSpotRequest.containsKey("address")) {
                scenicSpot.setAddress((String) scenicSpotRequest.get("address"));
            }
            
            if (scenicSpotRequest.containsKey("place")) {
                scenicSpot.setPlace((String) scenicSpotRequest.get("place"));
            }
            
            if (scenicSpotRequest.containsKey("count")) {
                Object countObj = scenicSpotRequest.get("count");
                if (countObj instanceof Number) {
                    scenicSpot.setCount(((Number) countObj).doubleValue());
                }
            }
            
            if (scenicSpotRequest.containsKey("price")) {
                Object priceObj = scenicSpotRequest.get("price");
                if (priceObj instanceof Number) {
                    scenicSpot.setPrice(((Number) priceObj).doubleValue());
                }
            }
            
            if (scenicSpotRequest.containsKey("status")) {
                Boolean status = (Boolean) scenicSpotRequest.get("status");
                scenicSpot.setStatus(status != null ? status : true);
            }
            
            // 保存景区信息
            ScenicSpot savedScenicSpot = scenicSpotService.saveScenicSpot(scenicSpot);
            
            // 返回创建的景区信息（全部使用小写字段名）
            Map<String, Object> scenicSpotInfo = new HashMap<>();
            scenicSpotInfo.put("id", savedScenicSpot.getId());
            scenicSpotInfo.put("title", savedScenicSpot.getTitle());
            scenicSpotInfo.put("image", savedScenicSpot.getImage());
            scenicSpotInfo.put("tags", savedScenicSpot.getTags());
            scenicSpotInfo.put("isrecommended", savedScenicSpot.getIsRecommended());
            scenicSpotInfo.put("introduction", savedScenicSpot.getIntroduction());
            scenicSpotInfo.put("opentime", savedScenicSpot.getOpenTime());
            scenicSpotInfo.put("address", savedScenicSpot.getAddress());
            scenicSpotInfo.put("place", savedScenicSpot.getPlace());
            scenicSpotInfo.put("count", savedScenicSpot.getCount());
            scenicSpotInfo.put("price", savedScenicSpot.getPrice());
            scenicSpotInfo.put("status", savedScenicSpot.getStatus());
            scenicSpotInfo.put("createtime", savedScenicSpot.getCreateTime());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("jingqu", scenicSpotInfo);
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("创建景区信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除景区信息（管理员功能）
     * 接口路径: POST /admin/jingqu/delete
     * 请求头: token: jwt_token_string
     */
    @PostMapping("/jingqu/delete")
    public ApiResponse<Map<String, Object>> deleteScenicSpot(@RequestHeader("token") String token,
                                                           @RequestBody Map<String, Object> deleteRequest) {
        try {
            // 验证token有效性
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("Token无效");
            }
            
            // 检查管理员权限
            Long adminUserId = jwtUtil.getUserIdFromToken(token);
            if (!userService.isAdmin(adminUserId)) {
                return ApiResponse.unauthorized("无管理员权限");
            }
            
            // 从请求体中获取景区ID
            Object idObj = deleteRequest.get("id");
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
            
            // 检查景区是否存在
            ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(id);
            if (scenicSpot == null) {
                return ApiResponse.error("景区不存在");
            }
            
            // 删除景区信息
            scenicSpotService.deleteScenicSpot(id);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deleted", true);
            responseData.put("jingquid", id);
            responseData.put("title", scenicSpot.getTitle());
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("删除景区信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改景区信息（管理员功能）
     * 接口路径: POST /admin/jingqu/update
     * 请求头: token: jwt_token_string
     */
    @PostMapping("/jingqu/update")
    public ApiResponse<Map<String, Object>> updateScenicSpot(@RequestHeader("token") String token,
                                                           @RequestBody Map<String, Object> scenicSpotRequest) {
        try {
            // 验证token有效性
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("Token无效");
            }
            
            // 检查管理员权限
            Long adminUserId = jwtUtil.getUserIdFromToken(token);
            if (!userService.isAdmin(adminUserId)) {
                return ApiResponse.unauthorized("无管理员权限");
            }
            
            // 从请求体中获取景区ID
            Object idObj = scenicSpotRequest.get("id");
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
            
            // 获取要修改的景区
            ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(id);
            if (scenicSpot == null) {
                return ApiResponse.error("景区不存在");
            }
            
            // 更新景区信息（支持小写和驼峰两种字段名格式）
            if (scenicSpotRequest.containsKey("title")) {
                String title = (String) scenicSpotRequest.get("title");
                if (title != null && !title.isEmpty()) {
                    scenicSpot.setTitle(title);
                }
            }
            
            if (scenicSpotRequest.containsKey("image")) {
                String image = (String) scenicSpotRequest.get("image");
                if (image != null && !image.isEmpty()) {
                    scenicSpot.setImage(image);
                }
            }
            
            if (scenicSpotRequest.containsKey("tags")) {
                scenicSpot.setTags((String) scenicSpotRequest.get("tags"));
            }
            
            // 支持小写和驼峰两种格式的isRecommended字段
            if (scenicSpotRequest.containsKey("isrecommended")) {
                Boolean isRecommended = (Boolean) scenicSpotRequest.get("isrecommended");
                scenicSpot.setIsRecommended(isRecommended != null ? isRecommended : false);
            } else if (scenicSpotRequest.containsKey("isRecommended")) {
                Boolean isRecommended = (Boolean) scenicSpotRequest.get("isRecommended");
                scenicSpot.setIsRecommended(isRecommended != null ? isRecommended : false);
            }
            
            if (scenicSpotRequest.containsKey("introduction")) {
                scenicSpot.setIntroduction((String) scenicSpotRequest.get("introduction"));
            }
            
            // 支持小写和驼峰两种格式的openTime字段
            if (scenicSpotRequest.containsKey("opentime")) {
                scenicSpot.setOpenTime((String) scenicSpotRequest.get("opentime"));
            } else if (scenicSpotRequest.containsKey("openTime")) {
                scenicSpot.setOpenTime((String) scenicSpotRequest.get("openTime"));
            }
            
            if (scenicSpotRequest.containsKey("address")) {
                scenicSpot.setAddress((String) scenicSpotRequest.get("address"));
            }
            
            if (scenicSpotRequest.containsKey("place")) {
                scenicSpot.setPlace((String) scenicSpotRequest.get("place"));
            }
            
            if (scenicSpotRequest.containsKey("count")) {
                Object countObj = scenicSpotRequest.get("count");
                if (countObj instanceof Number) {
                    scenicSpot.setCount(((Number) countObj).doubleValue());
                }
            }
            
            if (scenicSpotRequest.containsKey("price")) {
                Object priceObj = scenicSpotRequest.get("price");
                if (priceObj instanceof Number) {
                    scenicSpot.setPrice(((Number) priceObj).doubleValue());
                }
            }
            
            if (scenicSpotRequest.containsKey("status")) {
                Boolean status = (Boolean) scenicSpotRequest.get("status");
                scenicSpot.setStatus(status != null ? status : true);
            }
            
            // 保存更新后的景区信息
            ScenicSpot updatedScenicSpot = scenicSpotService.saveScenicSpot(scenicSpot);
            
            // 返回更新后的景区信息（全部使用小写字段名）
            Map<String, Object> scenicSpotInfo = new HashMap<>();
            scenicSpotInfo.put("id", updatedScenicSpot.getId());
            scenicSpotInfo.put("title", updatedScenicSpot.getTitle());
            scenicSpotInfo.put("image", updatedScenicSpot.getImage());
            scenicSpotInfo.put("tags", updatedScenicSpot.getTags());
            scenicSpotInfo.put("isrecommended", updatedScenicSpot.getIsRecommended());
            scenicSpotInfo.put("introduction", updatedScenicSpot.getIntroduction());
            scenicSpotInfo.put("opentime", updatedScenicSpot.getOpenTime());
            scenicSpotInfo.put("address", updatedScenicSpot.getAddress());
            scenicSpotInfo.put("place", updatedScenicSpot.getPlace());
            scenicSpotInfo.put("count", updatedScenicSpot.getCount());
            scenicSpotInfo.put("price", updatedScenicSpot.getPrice());
            scenicSpotInfo.put("status", updatedScenicSpot.getStatus());
            scenicSpotInfo.put("createtime", updatedScenicSpot.getCreateTime());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("jingqu", scenicSpotInfo);
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("修改景区信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有景区信息（管理员功能）
     * 接口路径: GET /api/admin/jingqu/info
     * 请求头: token: jwt_token_string
     */
    @GetMapping("/jingqu/info")
    public ApiResponse<Map<String, Object>> getAllScenicSpots(@RequestHeader("token") String token) {
        try {
            // 验证token有效性
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("Token无效");
            }
            
            // 检查管理员权限
            Long adminUserId = jwtUtil.getUserIdFromToken(token);
            if (!userService.isAdmin(adminUserId)) {
                return ApiResponse.unauthorized("无管理员权限");
            }
            
            // 获取所有景区信息
            List<ScenicSpot> scenicSpots = scenicSpotService.getAllScenicSpots();
            
            // 转换景区信息（全部使用小写字段名）
            List<Map<String, Object>> scenicSpotList = scenicSpots.stream().map(scenicSpot -> {
                Map<String, Object> scenicSpotInfo = new HashMap<>();
                scenicSpotInfo.put("id", scenicSpot.getId());
                scenicSpotInfo.put("title", scenicSpot.getTitle());
                scenicSpotInfo.put("image", scenicSpot.getImage());
                scenicSpotInfo.put("tags", scenicSpot.getTags());
                scenicSpotInfo.put("isrecommended", scenicSpot.getIsRecommended());
                scenicSpotInfo.put("introduction", scenicSpot.getIntroduction());
                scenicSpotInfo.put("opentime", scenicSpot.getOpenTime());
                scenicSpotInfo.put("address", scenicSpot.getAddress());
                scenicSpotInfo.put("place", scenicSpot.getPlace());
                scenicSpotInfo.put("count", scenicSpot.getCount());
                scenicSpotInfo.put("price", scenicSpot.getPrice());
                scenicSpotInfo.put("status", scenicSpot.getStatus());
                scenicSpotInfo.put("createtime", scenicSpot.getCreateTime());
                return scenicSpotInfo;
            }).collect(Collectors.toList());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("jingqu", scenicSpotList);
            responseData.put("total", scenicSpotList.size());
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("获取景区列表失败: " + e.getMessage());
        }
    }
}
