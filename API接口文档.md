# 景区应用后端API接口文档最终版

## 技术栈
- Spring Boot 3.5.5
- MyBatis Plus
- MySQL 8.0.43
- 数据库名：jingqu
- 端口：8080
- SDK：21.0.8

## 统一响应格式
所有接口都遵循以下响应格式：
```json
{
  "code": 1,      // 1-成功，0-失败
  "msg": "success", // 提示信息
  "data": {}      // 返回数据
}
```

## 错误码说明
- 1: 成功
- 0: 失败
- 401: 未授权/Token无效
- 500: 服务器内部错误

## 1. 认证模块 (AuthController)

### 1.1 用户登录
**接口路径**: `POST /api/login`  
**请求方式**: POST  

**请求参数**:
```json
{
  "code": "微信登录code（必填）",
  "userInfo": {
    "nickName": "用户昵称（可选）",
    "avatarUrl": "头像URL（可选）",
    "email": "邮箱地址（可选）",
    "phone": "手机号码（可选）"
  }
}
```

**响应数据**:
```json
{
  "token": "jwt_token_string",
  "userInfo": {
    "nickName": "用户昵称",
    "avatarUrl": "头像URL",
    "email": "邮箱地址",
    "phone": "手机号码"
  },
  "flag": 1  // 1-登录成功，2-需要完善信息
}
```

### 1.2 获取用户信息
**接口路径**: `GET /api/getUserInfo`  
**请求方式**: GET  
**请求头**: `token: jwt_token_string`

**响应数据**:
```json
{
  "id": 1,
  "openid": "微信openid",
  "nickName": "用户昵称",
  "avatarUrl": "头像URL",
  "email": "邮箱地址",
  "phone": "手机号码",
  "createTime": "2025-09-18T14:39:00",
  "updateTime": "2025-09-18T14:39:00"
}
```

## 2. 用户模块 (UserController)

### 2.1 获取轮播图
**接口路径**: `GET /api/user/getBanner`  
**请求方式**: GET  

**响应数据**:
```json
{
  "bannerList": [
    {
      "image": "https://example.com/banner1.jpg",
      "title": "轮播图标题1"
    },
    {
      "image": "https://example.com/banner2.jpg",
      "title": "轮播图标题2"
    }
  ]
}
```

### 2.2 获取首页景区列表
**接口路径**: `GET /api/user/getHomeList`  
**请求方式**: GET  

**响应数据**:
```json
[
  {
    "id": 1,
    "title": "天坛公园",
    "img": "/static/tt.jpg",
    "tag": ["著名", "名胜古迹"],
    "isDot": "推荐",
    "dot": true,
    "introduce": "天坛公园介绍...",
    "times": "每周一到周五9:00 -- 18:00开放",
    "isPlay": false,
    "address": ["116.410886", "39.881949"],
    "place": "具体位置",
    "count": 100.0,
    "price": 50.0
  }
]
```

### 2.3 获取按评分排序的景区列表
**接口路径**: `GET /api/user/getcountList`  
**请求方式**: GET  

**响应数据**:
```json
[
  {
    "id": 1,
    "title": "天坛公园",
    "image": "/static/tt.jpg",
    "tags": ["著名", "名胜古迹"],
    "isRecommended": true,
    "introduction": "天坛公园介绍...",
    "openTime": "每周一到周五9:00 -- 18:00开放",
    "address": "北京市东城区天坛路甲1号",
    "place": "天坛公园",
    "count": 100.0,
    "price": 50.0,
    "status": true,
    "createTime": "2025-09-18T15:07:00"
  }
]
```

### 2.4 获取相似景区推荐
**接口路径**: `POST /jingqu/similar`  
**请求方式**: POST  

**请求参数**:
```json
{
  "id": 1  // 景区ID
}
```

**响应数据**:
```json
[
  {
    "id": 2,
    "title": "北京故宫",
    "image": "/static/gg.jpeg",
    "tags": ["著名", "名胜古迹"],
    "isrecommended": true,
    "introduction": "北京故宫介绍...",
    "opentime": "每周一到周五9:00 -- 18:00开放",
    "address": "北京市东城区景山前街4号",
    "place": "故宫博物院",
    "count": 100.0,
    "price": 60.0,
    "status": true,
    "createtime": "2025-09-18T15:07:00"
  }
]
```

### 2.5 筛选景区
**接口路径**: `POST /api/user/filter`  
**请求方式**: POST  

**请求参数**:
```json
{
  "title": "天坛",           // 景区名称（模糊匹配，可选）
  "minCount": 80.0,        // 最低评分（可选）
  "maxCount": 100.0,       // 最高评分（可选）
  "minPrice": 0.0,         // 最低价格（可选）
  "maxPrice": 100.0,       // 最高价格（可选）
  "address": "北京"         // 地址（模糊匹配，可选）
}
```

**响应数据**:
```json
[
  {
    "id": 1,
    "title": "天坛公园",
    "image": "/static/tt.jpg",
    "tags": ["著名", "名胜古迹"],
    "isRecommended": true,
    "introduction": "天坛公园介绍...",
    "openTime": "每周一到周五9:00 -- 18:00开放",
    "address": "北京市东城区天坛路甲1号",
    "place": "天坛公园",
    "count": 100.0,
    "price": 50.0,
    "status": true,
    "createTime": "2025-09-18T15:07:00"
  }
]
```

## 3. 详情模块 (DetailController)

### 3.1 获取游玩项目
**接口路径**: `GET /api/detail/project`  
**请求方式**: GET  
**请求参数**: `scenicSpotId` (可选，景区ID)

**响应数据**:
```json
[
  {
    "id": 1,
    "belong": 1,
    "title": "游玩项目1",
    "url": "项目图片URL",
    "tag": "推荐",
    "desc": "项目描述",
    "latitude": 39.881949,
    "longitude": 116.410886
  }
]
```

### 3.2 获取相似景区推荐
**接口路径**: `GET /api/detail/similar-scenic-spots`  
**请求方式**: GET  
**请求参数**: `id` (景区ID)

**响应数据**:
```json
[
  {
    "id": 2,
    "title": "相似景区名称",
    "image": "景区图片URL",
    "tags": ["标签1", "标签2"],
    "isRecommended": true,
    "introduction": "景区介绍",
    "openTime": "开放时间",
    "address": "景区地址"
  }
]
```

## 4. 项目模块 (ProjectController)

### 4.1 获取项目详情
**接口路径**: `POST /api/project/info`  
**请求方式**: POST  

**请求参数**:
```json
{
  "id": 1  // 项目ID
}
```

**响应数据**:
```json
{
  "id": 1,
  "scenicSpot_Id": 1,
  "title": "项目详情标题",
  "content": "项目详细内容...",
  "images": ["图片1", "图片2"],
  "price": 100.00,
  "duration": "2小时",
  "location": [116.410886, 39.881949],
  "similiar": [
    {
      "title": "相似项目标题",
      "image": "相似项目图片URL"
    }
  ]
}
```

## 5. 点赞收藏模块 (LikeController)

### 5.1 获取收藏列表
**接口路径**: `GET /api/like/list`  
**请求方式**: GET  
**请求头**: `token: jwt_token_string`

**响应数据**:
```json
{
  "jingqu": [
    {
      "id": 1,
      "title": "景区名称",
      "img": "景区图片URL",
      "introduce": "景区介绍",
      "isDot": "推荐",
      "createTime": "2025-09-18T14:39:00"
    }
  ]
}
```

### 5.2 切换点赞状态
**接口路径**: `POST /api/like/toggle`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "scenic_spot_id": 1  // 景区ID
}
```

**响应数据**:
```json
{
  "liked": true  // true-已点赞，false-未点赞
}
```

### 5.3 检查点赞状态
**接口路径**: `POST /api/like/status`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "scenic_spot_id": 1  // 景区ID
}
```

**响应数据**:
```json
{
  "liked": true  // true-已点赞，false-未点赞
}
```

### 5.4 获取喜欢标签状态
**接口路径**: `POST /api/like/gettag`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "id": 1  // 景区ID
}
```

**响应数据**:
```json
{
  "tag": 1  // 0-不喜欢，1-喜欢
}
```

### 5.5 切换喜欢标签状态
**接口路径**: `POST /api/like/changetag`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "id": 1  // 景区ID
}
```

**响应数据**:
```json
{
  "tag": 1  // 0-不喜欢，1-喜欢（切换后的状态）
}
```

## 6. 管理员模块 (AdminController)

### 6.1 检查管理员状态
**接口路径**: `GET /api/admin`  
**请求方式**: GET  
**请求头**: `token: jwt_token_string`

**响应数据**:
```json
{
  "isadmin": true  // true-是管理员，false-不是管理员
}
```

### 6.2 获取所有用户信息
**接口路径**: `GET /api/admin/users/info`  
**请求方式**: GET  
**请求头**: `token: jwt_token_string`

**响应数据**:
```json
{
  "users": [
    {
      "id": 1,
      "nickName": "用户昵称",
      "avatarUrl": "头像URL",
      "email": "邮箱地址",
      "phone": "手机号码",
      "createTime": "2025-09-18T14:39:00",
      "updateTime": "2025-09-18T14:39:00",
      "isAdmin": false
    }
  ],
  "total": 1
}
```

### 6.3 删除用户
**接口路径**: `POST /api/admin/user/delete`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "id": 1  // 用户ID
}
```

**响应数据**:
```json
{
  "deleted": true,
  "userId": 1
}
```

### 6.4 修改用户信息
**接口路径**: `POST /api/admin/user/update`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "id": 1,  // 用户ID（必填）
  "nickName": "新昵称（可选）",
  "avatarUrl": "新头像URL（可选）",
  "email": "新邮箱地址（可选）",
  "phone": "新手机号码（可选）",
  "isAdmin": true  // 是否设置为管理员（可选）
}
```

**响应数据**:
```json
{
  "user": {
    "id": 1,
    "nickName": "新昵称",
    "avatarUrl": "新头像URL",
    "email": "新邮箱地址",
    "phone": "新手机号码",
    "isAdmin": true,
    "createTime": "2025-09-18T14:39:00",
    "updateTime": "2025-09-18T14:56:00"
  }
}
```

## 7. 景区管理模块 (ScenicSpotAdminController)

### 7.1 创建景区信息
**接口路径**: `POST /api/admin/jingqu/add`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "title": "景区名称（必填）",
  "image": "景区图片URL（必填）",
  "tags": "标签，逗号分隔（可选）",
  "isrecommended": true,  // 是否推荐（可选）
  "introduction": "景区介绍（可选）",
  "opentime": "开放时间（可选）",
  "address": "地址（可选）",
  "place": "具体位置（可选）",
  "count": 100.0,  // 数量（可选）
  "price": 50.0,   // 价格（可选）
  "status": true   // 状态：true-启用，false-禁用（可选）
}
```

**响应数据**:
```json
{
  "scenicSpot": {
    "id": 1,
    "title": "景区名称",
    "image": "景区图片URL",
    "tags": "标签，逗号分隔",
    "isrecommended": true,
    "introduction": "景区介绍",
    "opentime": "开放时间",
    "address": "地址",
    "place": "具体位置",
    "count": 100.0,
    "price": 50.0,
    "status": true,
    "createtime": "2025-09-18T15:07:00"
  }
}
```

### 7.2 删除景区信息
**接口路径**: `POST /api/admin/jingqu/delete`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "id": 1  // 景区ID
}
```

**响应数据**:
```json
{
  "deleted": true,
  "jingquid": 1,
  "title": "景区名称"
}
```

### 7.3 修改景区信息
**接口路径**: `POST /api/admin/jingqu/update`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "id": 1,  // 景区ID（必填）
  "title": "新景区名称（可选）",
  "image": "新景区图片URL（可选）",
  "tags": "新标签，逗号分隔（可选）",
  "isrecommended": true,  // 是否推荐（可选）
  "introduction": "新景区介绍（可选）",
  "opentime": "新开放时间（可选）",
  "address": "新地址（可选）",
  "place": "新具体位置（可选）",
  "count": 100.0,  // 新数量（可选）
  "price": 50.0,   // 新价格（可选）
  "status": true   // 新状态：true-启用，false-禁用（可选）
}
```

**响应数据**:
```json
{
  "jingqu": {
    "id": 1,
    "title": "新景区名称",
    "image": "新景区图片URL",
    "tags": "新标签，逗号分隔",
    "isrecommended": true,
    "introduction": "新景区介绍",
    "opentime": "新开放时间",
    "address": "新地址",
    "place": "新具体位置",
    "count": 100.0,
    "price": 50.0,
    "status": true,
    "createtime": "2025-09-18T15:07:00"
  }
}
```

### 7.4 获取所有景区信息
**接口路径**: `GET /api/admin/jingqu/info`  
**请求方式**: GET  
**请求头**: `token: jwt_token_string`

**响应数据**:
```json
{
  "scenicSpots": [
    {
      "id": 1,
      "title": "景区名称",
      "image": "景区图片URL",
      "tags": "标签，逗号分隔",
      "isRecommended": true,
      "introduction": "景区介绍",
      "openTime": "开放时间",
      "address": "地址",
      "place": "具体位置",
      "count": 100.0,
      "price": 50.0,
      "status":
