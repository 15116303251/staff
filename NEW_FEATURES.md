# 新增账户管理功能

在现有分支上添加了以下新功能来完善账户管理系统：

## 1. 账户搜索功能 (`POST /v1/account/search`)

### 功能描述
支持按多种条件搜索账户，包括姓名、邮箱、电话号码和激活状态。

### 请求参数
```json
{
  "name": "张三",           // 可选，按姓名搜索（模糊匹配）
  "email": "example",      // 可选，按邮箱搜索（模糊匹配）
  "phoneNumber": "138",    // 可选，按电话号码搜索（模糊匹配）
  "confirmedAndActive": true, // 可选，按激活状态筛选
  "offset": 0,             // 分页偏移量，默认0
  "limit": 10              // 每页数量，默认10
}
```

### 权限要求
- `AUTHORIZATION_SUPPORT_USER`

## 2. 账户统计功能 (`GET /v1/account/stats`)

### 功能描述
获取账户系统的统计信息，包括总数、活跃数、创建趋势等。

### 响应示例
```json
{
  "totalAccounts": 150,
  "activeAccounts": 120,
  "inactiveAccounts": 30,
  "accountsWithEmail": 140,
  "accountsWithPhone": 100,
  "accountsCreatedToday": 5,
  "accountsCreatedThisWeek": 25,
  "accountsCreatedThisMonth": 80
}
```

### 权限要求
- `AUTHORIZATION_SUPPORT_USER`

## 3. 账户批量操作功能 (`POST /v1/account/batch`)

### 功能描述
支持对多个账户进行批量操作，包括激活、停用、删除和导出。

### 请求参数
```json
{
  "operation": "ACTIVATE",  // 操作类型：ACTIVATE, DEACTIVATE, DELETE, EXPORT
  "accountIds": ["id1", "id2", "id3"],  // 账户ID列表
  "reason": "批量激活用户"  // 可选，操作原因
}
```

### 响应示例
```json
{
  "message": "Batch operation completed. Success: 3, Failed: 0"
}
```

### 权限要求
- `AUTHORIZATION_SUPPORT_USER`

## 技术实现细节

### 1. 新增DTO类
- `SearchAccountRequest`: 搜索请求参数
- `AccountStatsResponse`: 统计响应数据
- `BatchAccountRequest`: 批量操作请求参数

### 2. 新增Repository方法
- `countByConfirmedAndActive()`: 按激活状态统计
- `countByEmailIsNotNull()`: 统计有邮箱的账户
- `countByPhoneNumberIsNotNull()`: 统计有电话的账户
- `countByMemberSinceAfter()`: 统计指定时间后创建的账户

### 3. 新增Service方法
- `searchAccounts()`: 实现账户搜索逻辑
- `getAccountStats()`: 计算账户统计信息
- `batchOperation()`: 执行批量操作

### 4. 新增Controller端点
- `POST /v1/account/search`: 搜索账户
- `GET /v1/account/stats`: 获取统计信息
- `POST /v1/account/batch`: 批量操作账户

## 使用示例

### 搜索账户
```bash
curl -X POST http://localhost:8080/v1/account/search \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "张",
    "limit": 20
  }'
```

### 获取统计信息
```bash
curl -X GET http://localhost:8080/v1/account/stats \
  -H "Authorization: Bearer {token}"
```

### 批量激活账户
```bash
curl -X POST http://localhost:8080/v1/account/batch \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "ACTIVATE",
    "accountIds": ["user1", "user2", "user3"],
    "reason": "季度激活活动"
  }'
```

## 注意事项

1. 搜索功能目前使用内存过滤，对于大数据量建议实现数据库层面的搜索优化
2. 批量删除操作不可逆，请谨慎使用
3. 统计功能的时间范围计算基于当前系统时间
4. 所有新增功能都包含完整的审计日志记录