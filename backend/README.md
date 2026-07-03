# 后端运行说明

## 环境要求

- JDK 17
- Maven 3.8+
- openGauss 数据库已按 `design/sql` 脚本初始化

## 启动步骤

1. 按实际数据库设置环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`、`JWT_SECRET`。当前开发环境默认连接 `jdbc:opengauss://192.168.126.131:8888/dengjxmis13`，其他环境必须用 `DB_URL` 覆盖。
2. 执行 `mvn spring-boot:run`。
3. 访问 `http://localhost:8080/api/auth/login` 验证登录接口。

不要在配置文件或文档中写入真实数据库密码、token、私钥或 `.env` 实际值。

## 验证命令

```powershell
mvn test
```

## 代码结构

后端按 Spring Boot 分层组织：

- `controller`：HTTP 接口入口。
- `service`：业务服务接口。
- `service.impl`：业务服务实现类。
- `mapper`：MyBatis Plus 数据访问接口。
- `entity`：数据库表实体。
- `dto`：请求和响应 DTO。
- `config`：框架配置类。
- `common`：通用响应、分页和异常处理。
- `security`：JWT、认证用户和当前用户上下文。

## 异常返回说明

后端统一使用 `ApiResponse` 返回异常信息：`success=false`，`message` 为可直接展示的错误摘要，`data` 为错误定位详情。`data` 中包含 `traceId`、`code`、`path`、`method`、`exception`、`rootCause`、`location`、`details` 和 `timestamp`，前端可把 `traceId` 提供给后端对照控制台日志排查。

控制器内异常由 `GlobalExceptionHandler` 捕捉；认证失败和权限不足由 `SecurityConfig` 返回同样结构。业务代码需要主动抛出可预期错误时，优先使用 `BusinessException`，可按需传入自定义 `code` 和 `details`。

## 配置说明

- `DB_URL`：openGauss JDBC 地址。注意 SQL 中未加引号创建的数据库名会折叠为小写，连接课程库时应使用 `dengjxmis13`。
- `DB_USERNAME`：数据库用户名。
- `DB_PASSWORD`：数据库密码。
- `JWT_SECRET`：JWT 签名密钥，生产环境必须设置为强随机值。
- 控制台日志默认只显示 `WARN` 及以上级别，避免 openGauss JDBC 等第三方组件输出大量连接 `INFO` 日志。

## 数据库映射约定

openGauss 会把未加双引号的表名和列名折叠为小写。实体类中的 `@TableName`、`@TableId`、`@TableField` 必须填写数据库实际小写标识符，例如 `dengjx_grades13`、`djx_gradeid13`，避免 MyBatis Plus 插入后获取自增主键时引用不存在的混合大小写列名。
