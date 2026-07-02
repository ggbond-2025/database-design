# 后端运行说明

## 环境要求

- JDK 17
- Maven 3.8+
- openGauss 数据库已按 `design/sql` 脚本初始化

## 启动步骤

1. 设置环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`、`JWT_SECRET`。
2. 执行 `mvn spring-boot:run`。
3. 访问 `http://localhost:8080/api/auth/login` 验证登录接口。

不要在配置文件或文档中写入真实数据库密码、token、私钥或 `.env` 实际值。
