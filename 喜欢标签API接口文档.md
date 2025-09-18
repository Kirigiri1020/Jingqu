# 喜欢标签API接口文档

## 接口列表

### 1. 获取景区喜欢标签状态
**接口路径**: `/api/like/gettag`  
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
  "code": 1,
  "msg": "success",
  "data": {
    "tag": 1  // 0-不喜欢，1-喜欢
  }
}
```

### 2. 切换景区喜欢标签状态
**接口路径**: `/api/like/changetag`  
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
  "code": 1,
  "msg": "success",
  "data": {
    "tag": 1  // 0-不喜欢，1-喜欢（切换后的状态）
  }
}
```

## 接口说明

### gettag 接口
- **功能**: 查询用户对指定景区的喜欢状态
- **参数**: `id` - 景区ID
- **返回值**: `tag` - 0表示不喜欢，1表示喜欢
- **认证**: 需要用户登录token

### changetag 接口  
- **功能**: 切换用户对指定景区的喜欢状态
- **参数**: `id` - 景区ID
- **返回值**: `tag` - 切换后的状态（0或1）
- **认证**: 需要用户登录token
- **业务逻辑**: 如果当前状态是0，切换为1；如果当前状态是1，切换为0

## 使用示例

### 前端调用示例
```javascript
// 获取喜欢状态
const getLikeStatus = async (scenicId) => {
  const res = await getliketag(scenicId);
  if (res.data && res.data.tag !== undefined) {
    return res.data.tag; // 0 或 1
  }
  return 0;
}

// 切换喜欢状态
const toggleLikeStatus = async (scenicId) => {
  const res = await changeliketag(scenicId);
  if (res.data && res.data.tag !== undefined) {
    return res.data.tag; // 切换后的状态
  }
  return 0;
}
```

## 错误处理
- **401错误**: token无效或未登录
- **404错误**: 景区不存在
- **500错误**: 服务器内部错误

所有接口都遵循统一的响应格式：
```json
{
  "code": 1,      // 1-成功，0-失败
  "msg": "success", // 提示信息
  "data": {}      // 返回数据
}
