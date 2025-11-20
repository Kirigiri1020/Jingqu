package com.example.jingqu.controller;

import com.example.jingqu.common.ApiResponse;
import com.example.jingqu.entity.Project;
import com.example.jingqu.entity.User;
import com.example.jingqu.service.ProjectService;
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
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取所有用户信息（排除openid字段）
     * 接口路径: GET /api/admin/users
     * 请求头: token: jwt_token_string
     */
    /**
     * 检查用户是否为管理员
     * 接口路径: GET /api/admin
     * 请求头: token: jwt_token_string
     */
    @GetMapping("")
    public ApiResponse<Map<String, Object>> checkAdminStatus(@RequestHeader("token") String token) {
        try {
            // 验证token有效性
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("Token无效");
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean isAdmin = userService.isAdmin(userId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("isadmin", isAdmin);
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("检查管理员状态失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/users/info")
    public ApiResponse<Map<String, Object>> getAllUsers(@RequestHeader("token") String token) {
        try {
            // 验证token有效性
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("Token无效");
            }
            
            // 检查管理员权限
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (!userService.isAdmin(userId)) {
                return ApiResponse.unauthorized("无管理员权限");
            }
            
            // 获取所有用户
            List<User> users = userService.getAllUsers();
            
            // 转换用户信息，排除openid字段，全部使用小写字段名
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("nickname", user.getNickName());
                userInfo.put("avatarurl", user.getAvatarUrl());
                userInfo.put("email", user.getEmail());
                userInfo.put("phone", user.getPhone());
                userInfo.put("createtime", user.getCreateTime());
                userInfo.put("updatetime", user.getUpdateTime());
                userInfo.put("isadmin", user.getIsAdmin());
                return userInfo;
            }).collect(Collectors.toList());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("users", userList);
            responseData.put("total", userList.size());
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户（管理员功能）
     * 接口路径: POST /api/admin/user/delete
     * 请求头: token: jwt_token_string
     */
    @PostMapping("/user/delete")
    public ApiResponse<Map<String, Object>> deleteUser(@RequestHeader("token") String token,
                                                     @RequestBody Map<String, Long> request) {
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
            
            // 获取用户ID
            Long id = request.get("id");
            if (id == null) {
                return ApiResponse.error("用户ID不能为空");
            }
            
            // 不能删除自己
            if (adminUserId.equals(id)) {
                return ApiResponse.error("不能删除自己的账户");
            }
            
            // 删除用户
            boolean deleted = userService.deleteUser(id);
            
            if (!deleted) {
                return ApiResponse.error("用户不存在或删除失败");
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deleted", true);
            responseData.put("userid", id);
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("删除用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改用户信息（管理员功能）
     * 接口路径: POST /api/admin/user/update
     * 请求头: token: jwt_token_string
     */
    @PostMapping("/user/update")
    public ApiResponse<Map<String, Object>> updateUser(@RequestHeader("token") String token,
                                                     @RequestBody Map<String, Object> userRequest) {
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
            
            // 获取用户ID
            Object idObj = userRequest.get("id");
            if (idObj == null) {
                return ApiResponse.error("用户ID不能为空");
            }
            Long id;
            if (idObj instanceof Number) {
                id = ((Number) idObj).longValue();
            } else if (idObj instanceof String) {
                try {
                    id = Long.parseLong((String) idObj);
                } catch (NumberFormatException e) {
                    return ApiResponse.error("用户ID格式不正确");
                }
            } else {
                return ApiResponse.error("用户ID格式不正确");
            }
            
            // 获取要修改的用户
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            
            // 更新用户信息（支持小写字段名）
            if (userRequest.containsKey("nickname") || userRequest.containsKey("nickName")) {
                String nickName = (String) (userRequest.containsKey("nickname") ? 
                    userRequest.get("nickname") : userRequest.get("nickName"));
                user.setNickName(nickName);
            }
            
            if (userRequest.containsKey("avatarurl") || userRequest.containsKey("avatarUrl")) {
                String avatarUrl = (String) (userRequest.containsKey("avatarurl") ? 
                    userRequest.get("avatarurl") : userRequest.get("avatarUrl"));
                user.setAvatarUrl(avatarUrl);
            }
            
            if (userRequest.containsKey("email")) {
                user.setEmail((String) userRequest.get("email"));
            }
            
            if (userRequest.containsKey("phone")) {
                user.setPhone((String) userRequest.get("phone"));
            }
            
            if (userRequest.containsKey("isadmin") || userRequest.containsKey("isAdmin")) {
                Boolean isAdmin = (Boolean) (userRequest.containsKey("isadmin") ? 
                    userRequest.get("isadmin") : userRequest.get("isAdmin"));
                user.setIsAdmin(isAdmin != null ? isAdmin : false);
            }
            
            // 保存更新后的用户信息
            User updatedUser = userService.updateUser(user);
            
            // 返回更新后的用户信息（排除openid，全部使用小写字段名）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", updatedUser.getId());
            userInfo.put("nickname", updatedUser.getNickName());
            userInfo.put("avatarurl", updatedUser.getAvatarUrl());
            userInfo.put("email", updatedUser.getEmail());
            userInfo.put("phone", updatedUser.getPhone());
            userInfo.put("isadmin", updatedUser.getIsAdmin());
            userInfo.put("createtime", updatedUser.getCreateTime());
            userInfo.put("updatetime", updatedUser.getUpdateTime());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", userInfo);
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("修改用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有游玩项目信息（管理员功能）
     * 接口路径: GET /api/admin/project/info
     * 请求头: token: jwt_token_string
     */
    @GetMapping("/project/info")
    public ApiResponse<Map<String, Object>> getAllProjects(@RequestHeader("token") String token) {
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
            
            // 获取所有游玩项目信息
            List<Project> projects = projectService.getAllProjects();
            
            // 转换游玩项目信息（全部使用小写字段名）
            List<Map<String, Object>> projectList = projects.stream().map(project -> {
                Map<String, Object> projectInfo = new HashMap<>();
                projectInfo.put("id", project.getId());
                projectInfo.put("scenicspotid", project.getScenicSpotId());
                projectInfo.put("title", project.getTitle());
                projectInfo.put("image", project.getImage());
                projectInfo.put("tag", project.getTag());
                projectInfo.put("description", project.getDescription());
                projectInfo.put("latitude", project.getLatitude());
                projectInfo.put("longitude", project.getLongitude());
                projectInfo.put("status", project.getStatus());
                projectInfo.put("createtime", project.getCreateTime());
                return projectInfo;
            }).collect(Collectors.toList());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("projects", projectList);
            responseData.put("total", projectList.size());
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("获取游玩项目列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建游玩项目（管理员功能）
     * 接口路径: POST /api/admin/project/add
     * 请求头: token: jwt_token_string
     */
    @PostMapping("/project/add")
    public ApiResponse<Map<String, Object>> createProject(@RequestHeader("token") String token,
                                                        @RequestBody Map<String, Object> projectRequest) {
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
            String title = (String) projectRequest.get("title");
            String image = (String) projectRequest.get("image");
            Object scenicSpotIdObj = projectRequest.get("scenicspotid");
            
            if (title == null || title.isEmpty()) {
                return ApiResponse.error("项目名称不能为空");
            }
            
            if (image == null || image.isEmpty()) {
                return ApiResponse.error("项目图片不能为空");
            }
            
            if (scenicSpotIdObj == null) {
                return ApiResponse.error("景区ID不能为空");
            }
            
            Long scenicSpotId;
            if (scenicSpotIdObj instanceof Number) {
                scenicSpotId = ((Number) scenicSpotIdObj).longValue();
            } else if (scenicSpotIdObj instanceof String) {
                try {
                    scenicSpotId = Long.parseLong((String) scenicSpotIdObj);
                } catch (NumberFormatException e) {
                    return ApiResponse.error("景区ID格式不正确");
                }
            } else {
                return ApiResponse.error("景区ID格式不正确");
            }
            
            // 创建游玩项目对象
            Project project = new Project();
            project.setScenicSpotId(scenicSpotId);
            project.setTitle(title);
            project.setImage(image);
            
            // 设置可选字段（全部使用小写字段名）
            if (projectRequest.containsKey("tag")) {
                project.setTag((String) projectRequest.get("tag"));
            }
            
            if (projectRequest.containsKey("description")) {
                project.setDescription((String) projectRequest.get("description"));
            }
            
            if (projectRequest.containsKey("latitude")) {
                Object latitudeObj = projectRequest.get("latitude");
                if (latitudeObj instanceof Number) {
                    project.setLatitude(java.math.BigDecimal.valueOf(((Number) latitudeObj).doubleValue()));
                }
            }
            
            if (projectRequest.containsKey("longitude")) {
                Object longitudeObj = projectRequest.get("longitude");
                if (longitudeObj instanceof Number) {
                    project.setLongitude(java.math.BigDecimal.valueOf(((Number) longitudeObj).doubleValue()));
                }
            }
            
            if (projectRequest.containsKey("status")) {
                Boolean status = (Boolean) projectRequest.get("status");
                project.setStatus(status != null ? status : true);
            }
            
            // 保存游玩项目信息
            Project savedProject = projectService.saveProject(project);
            
            // 返回创建的游玩项目信息（全部使用小写字段名）
            Map<String, Object> projectInfo = new HashMap<>();
            projectInfo.put("id", savedProject.getId());
            projectInfo.put("scenicspotid", savedProject.getScenicSpotId());
            projectInfo.put("title", savedProject.getTitle());
            projectInfo.put("image", savedProject.getImage());
            projectInfo.put("tag", savedProject.getTag());
            projectInfo.put("description", savedProject.getDescription());
            projectInfo.put("latitude", savedProject.getLatitude());
            projectInfo.put("longitude", savedProject.getLongitude());
            projectInfo.put("status", savedProject.getStatus());
            projectInfo.put("createtime", savedProject.getCreateTime());
            
            return ApiResponse.success(projectInfo);
            
        } catch (Exception e) {
            return ApiResponse.error("创建游玩项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改游玩项目（管理员功能）
     * 接口路径: POST /api/admin/project/update
     * 请求头: token: jwt_token_string
     */
    @PostMapping("/project/update")
    public ApiResponse<Map<String, Object>> updateProject(@RequestHeader("token") String token,
                                                        @RequestBody Map<String, Object> projectRequest) {
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
            
            // 从请求体中获取项目ID
            Object idObj = projectRequest.get("id");
            if (idObj == null) {
                return ApiResponse.error("项目ID不能为空");
            }
            
            Long id;
            if (idObj instanceof Number) {
                id = ((Number) idObj).longValue();
            } else if (idObj instanceof String) {
                try {
                    id = Long.parseLong((String) idObj);
                } catch (NumberFormatException e) {
                    return ApiResponse.error("项目ID格式不正确");
                }
            } else {
                return ApiResponse.error("项目ID格式不正确");
            }
            
            // 获取要修改的项目
            Project project = projectService.getProjectById(id);
            if (project == null) {
                return ApiResponse.error("游玩项目不存在");
            }
            
            // 更新项目信息（支持小写字段名）
            if (projectRequest.containsKey("title")) {
                String title = (String) projectRequest.get("title");
                if (title != null && !title.isEmpty()) {
                    project.setTitle(title);
                }
            }
            
            if (projectRequest.containsKey("image")) {
                String image = (String) projectRequest.get("image");
                if (image != null && !image.isEmpty()) {
                    project.setImage(image);
                }
            }
            
            if (projectRequest.containsKey("scenicspotid")) {
                Object scenicSpotIdObj = projectRequest.get("scenicspotid");
                if (scenicSpotIdObj != null) {
                    Long scenicSpotId;
                    if (scenicSpotIdObj instanceof Number) {
                        scenicSpotId = ((Number) scenicSpotIdObj).longValue();
                    } else if (scenicSpotIdObj instanceof String) {
                        try {
                            scenicSpotId = Long.parseLong((String) scenicSpotIdObj);
                        } catch (NumberFormatException e) {
                            return ApiResponse.error("景区ID格式不正确");
                        }
                    } else {
                        return ApiResponse.error("景区ID格式不正确");
                    }
                    project.setScenicSpotId(scenicSpotId);
                }
            }
            
            if (projectRequest.containsKey("tag")) {
                project.setTag((String) projectRequest.get("tag"));
            }
            
            if (projectRequest.containsKey("description")) {
                project.setDescription((String) projectRequest.get("description"));
            }
            
            if (projectRequest.containsKey("latitude")) {
                Object latitudeObj = projectRequest.get("latitude");
                if (latitudeObj instanceof Number) {
                    project.setLatitude(java.math.BigDecimal.valueOf(((Number) latitudeObj).doubleValue()));
                } else if (latitudeObj == null) {
                    project.setLatitude(null);
                }
            }
            
            if (projectRequest.containsKey("longitude")) {
                Object longitudeObj = projectRequest.get("longitude");
                if (longitudeObj instanceof Number) {
                    project.setLongitude(java.math.BigDecimal.valueOf(((Number) longitudeObj).doubleValue()));
                } else if (longitudeObj == null) {
                    project.setLongitude(null);
                }
            }
            
            if (projectRequest.containsKey("status")) {
                Boolean status = (Boolean) projectRequest.get("status");
                project.setStatus(status != null ? status : true);
            }
            
            // 保存更新后的项目信息
            Project updatedProject = projectService.saveProject(project);
            
            // 返回更新后的项目信息（全部使用小写字段名）
            Map<String, Object> projectInfo = new HashMap<>();
            projectInfo.put("id", updatedProject.getId());
            projectInfo.put("scenicspotid", updatedProject.getScenicSpotId());
            projectInfo.put("title", updatedProject.getTitle());
            projectInfo.put("image", updatedProject.getImage());
            projectInfo.put("tag", updatedProject.getTag());
            projectInfo.put("description", updatedProject.getDescription());
            projectInfo.put("latitude", updatedProject.getLatitude());
            projectInfo.put("longitude", updatedProject.getLongitude());
            projectInfo.put("status", updatedProject.getStatus());
            projectInfo.put("createtime", updatedProject.getCreateTime());
            
            return ApiResponse.success(projectInfo);
            
        } catch (Exception e) {
            return ApiResponse.error("修改游玩项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除游玩项目（管理员功能）
     * 接口路径: POST /api/admin/project/delete
     * 请求头: token: jwt_token_string
     */
    @PostMapping("/project/delete")
    public ApiResponse<Map<String, Object>> deleteProject(@RequestHeader("token") String token,
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
            
            // 从请求体中获取项目ID
            Object idObj = deleteRequest.get("id");
            if (idObj == null) {
                return ApiResponse.error("项目ID不能为空");
            }
            
            Long id;
            if (idObj instanceof Number) {
                id = ((Number) idObj).longValue();
            } else if (idObj instanceof String) {
                try {
                    id = Long.parseLong((String) idObj);
                } catch (NumberFormatException e) {
                    return ApiResponse.error("项目ID格式不正确");
                }
            } else {
                return ApiResponse.error("项目ID格式不正确");
            }
            
            // 检查项目是否存在
            Project project = projectService.getProjectById(id);
            if (project == null) {
                return ApiResponse.error("游玩项目不存在");
            }
            
            // 删除游玩项目
            projectService.deleteProject(id);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deleted", true);
            responseData.put("projectid", id);
            responseData.put("title", project.getTitle());
            
            return ApiResponse.success(responseData);
            
        } catch (Exception e) {
            return ApiResponse.error("删除游玩项目失败: " + e.getMessage());
        }
    }
}
