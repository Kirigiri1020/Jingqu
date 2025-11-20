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
      "status": true,
      "createTime": "2025-09-18T15:07:00"
    }
  ],
  "total": 1
}
```

## 8. 项目管理模块 (ProjectController)

### 8.1 创建项目信息
**接口路径**: `POST /api/admin/project/add`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "scenicspotid": 1,
  "title": "故宫博物院深度讲解",
  "image": "https://img2.baidu.com/it/u=781121407,1589415312&fm=253&app=138&f=JPEG?w=800&h=929",
  "tag": "文化体验",
  "description": "专业导游带领参观故宫各大殿宇，深度讲解明清皇家历史与文化",
  "latitude": 39.916344,
  "longitude": 116.397155,
  "status": true
}
```

**响应数据**:
```json
{
    "code": 1,
    "msg": "success",
    "data": {
        "image": "https://img2.baidu.com/it/u=781121407,1589415312&fm=253&app=138&f=JPEG?w=800&h=929",
        "createtime": "2025-09-18T18:37:36.7867167",
        "latitude": 39.916344,
        "description": "专业导游带领参观故宫各大殿宇，深度讲解明清皇家历史与文化",
        "id": 101,
        "tag": "文化体验",
        "title": "故宫博物院深度讲解",
        "scenicspotid": 1,
        "longitude": 116.397155,
        "status": true
    }
}
```

### 8.2 删除项目信息
**接口路径**: `POST /api/admin/project/delete`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "id": 101
}
```

**响应数据**:
```json
{
    "code": 1,
    "msg": "success",
    "data": {
        "deleted": true,
        "title": "故宫博物院深度讲解",
        "projectid": 101
    }
}
```

### 8.3 修改项目信息
**接口路径**: `POST /api/admin/project/update`  
**请求方式**: POST  
**请求头**: `token: jwt_token_string`

**请求参数**:
```json
{
  "id": 101,
  "scenicspotid": 2,
  "title": "故宫博物院深度讲解",
  "image": "https://img2.baidu.com/it/u=781121407,1589415312&fm=253&app=138&f=JPEG?w=800&h=929",
  "tag": "文化体验",
  "description": "专业导游带领参观故宫各大殿宇，深度讲解明清皇家历史与文化",
  "latitude": 39.916344,
  "longitude": 116.397155,
  "status": true
}
```

**响应数据**:
```json
{
    "code": 1,
    "msg": "success",
    "data": {
        "image": "https://img2.baidu.com/it/u=781121407,1589415312&fm=253&app=138&f=JPEG?w=800&h=929",
        "createtime": "2025-09-18T18:37:37",
        "latitude": 39.916344,
        "description": "专业导游带领参观故宫各大殿宇，深度讲解明清皇家历史与文化",
        "id": 101,
        "tag": "文化体验",
        "title": "故宫博物院深度讲解",
        "scenicspotid": 2,
        "longitude": 116.397155,
        "status": true
    }
}
```

### 8.4 获取所有项目信息
**接口路径**: `GET /api/admin/project/info`  
**请求方式**: GET  
**请求头**: `token: jwt_token_string`

**响应数据**:
```json
{
    "code": 1,
    "msg": "success",
    "data": {
        "total": 3,
        "projects": [
            {
                "image": "https://img2.baidu.com/it/u=781121407,1589415312&fm=253&app=138&f=JPEG?w=800&h=929",
                "createtime": "2025-09-16T15:56:19",
                "latitude": 39.916344,
                "description": "专业导游带领参观故宫各大殿宇，深度讲解明清皇家历史与文化",
                "id": 1,
                "tag": "文化体验",
                "title": "故宫博物院深度讲解",
                "scenicspotid": 1,
                "longitude": 116.397155,
                "status": true
            },
            {
                "image": "https://img2.baidu.com/it/u=781121407,1589415312&fm=253&app=138&f=JPEG?w=800&h=929",
                "createtime": "2025-09-16T15:56:19",
                "latitude": 39.916344,
                "description": "参观故宫珍宝馆，欣赏明清两代皇家珍宝与艺术品",
                "id": 2,
                "tag": "文物鉴赏",
                "title": "珍宝馆特展参观",
                "scenicspotid": 1,
                "longitude": 116.397155,
                "status": true
            },
            {
                "image": "https://img2.baidu.com/it/u=781121407,1589415312&fm=253&app=138&f=JPEG?w=800&h=929",
                "createtime": "2025-09-16T15:56:19",
                "latitude": 30.246026,
                "description": "乘坐古风游船游览西湖十景，欣赏湖光山色",
                "id": 3,
                "tag": "水上活动",
                "title": "西湖游船观光",
                "scenicspotid": 2,
                "longitude": 120.143222,
                "status": true
            }
        ]
    }
}
```

## 注意事项
1. 所有需要认证的接口都需要在请求头中添加 `token: jwt_token_string`
2. 管理员接口需要用户具有管理员权限
3. openid字段由后端自动生成，前端无需传递
4. 所有时间字段使用ISO 8601格式
5. 相似景区推荐基于标签匹配算法，支持多标签匹配
6. 评分排序接口按count字段降序排列

## 部署说明
1. 确保MySQL 8.0已安装并创建数据库`jingqu`
2. 应用配置文件`application.yml`中配置数据库连接
3. 启动端口设置为8080
4. 前端请求地址配置为`http://localhost:8080/api`
5. 需要配置微信小程序appid和secret用于登录功能

## 示例数据初始化SQL
```sql
-- 插入轮播图数据
INSERT INTO banner (image, title, sort, status) VALUES
('https://bpic.51yuansu.com/backgd/cover/00/06/69/5b684f270f79a.jpg', '身无彩凤双飞翼，心有灵犀一点通', 1, 1),
('https://img.yipic.cn/thumb/2ffb7e72/e5b68a08/eb5d7bc4/e3a15e30/big_2ffb7e72e5b68a08eb5d7bc4e3a15e30.png', '谁念西风独自凉，萧萧黄叶闭疏窗，沉思往事立残阳', 2, 1);

-- 插入景区数据
INSERT INTO scenic_spot (title, image, tags, is_recommended, introduction, open_time, address, place, count, price) VALUES
('天坛公园', '/static/tt.jpg', '著名,名胜古迹', 1, '天坛公园介绍...', '每周一到周五9:00 -- 18:00开放', '北京市东城区天坛路甲1号', '天坛公园', 100.0, 50.0),
('北京故宫', '/static/gg.jpeg', '著名,名胜古迹', 1, '北京故宫介绍...', '每周一到周五9:00 -- 18:00开放', '北京市东城区景山前街4号', '故宫博物院', 95.0, 60.0);
```

这个文档包含了景区应用后端的所有API接口，数据库表结构设计，以及详细的接口说明。您可以根据这个文档来开发和维护Spring Boot后端服务。
