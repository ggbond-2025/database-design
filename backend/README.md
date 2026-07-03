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

## 配置说明

- `DB_URL`：openGauss JDBC 地址。注意 SQL 中未加引号创建的数据库名会折叠为小写，连接课程库时应使用 `dengjxmis13`。
- `DB_USERNAME`：数据库用户名。
- `DB_PASSWORD`：数据库密码。
- `JWT_SECRET`：JWT 签名密钥，生产环境必须设置为强随机值。
