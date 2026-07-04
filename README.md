# 高校教务系统部署启动说明

本项目是前后端分离的高校教务系统：

- `backend`：Spring Boot 3.3.5 后端，使用 Maven 构建，数据库为 openGauss。
- `frontend`：Vue + Vite 前端，使用 npm 管理依赖。
- `design/sql`：数据库建库、建表、视图、触发器、存储过程和初始化数据脚本。
- `design/docs`：接口清单和验收说明。

## 1. 环境准备

部署前需要安装以下环境：

| 工具 | 要求 | 说明 |
| --- | --- | --- |
| JDK | 17 | 后端 `pom.xml` 指定 Java 17 |
| Maven | 3.8+ | 项目未提供 Maven Wrapper，需要本机安装 `mvn` |
| Node.js | 18+ 建议 | 用于安装和运行前端依赖 |
| npm | 随 Node.js 安装 | 前端存在 `package-lock.json`，建议使用 npm |
| openGauss | 可连接实例 | 后端 JDBC 驱动为 `org.opengauss.Driver` |

检查命令：

```powershell
java -version
mvn -version
node -v
npm -v
```

如果命令不存在，需要先安装对应工具，并确保命令已加入系统 `PATH`。

## 2. 获取项目代码

进入项目根目录：

```powershell
cd C:\Users\Lenovo\Desktop\数据库课程设计
```

后续命令默认都在该根目录下执行。

## 3. 初始化数据库

数据库脚本位于 `design/sql`，需要按顺序执行。

### 3.1 创建数据库

使用 openGauss 管理账号连接数据库服务后，先执行：

```sql
design/sql/01_create_database.sql
```

该脚本用于创建课程设计数据库。

### 3.2 连接业务数据库

建库后切换连接到业务数据库。注意：openGauss/PostgreSQL 中未加双引号的数据库名会折叠为小写，后端连接地址建议使用小写库名。

JDBC 地址格式：ip地址用自己部署opengauss的地址，端口号也是

```text
jdbc:opengauss://数据库主机:端口/数据库名
```

### 3.3 执行表结构和初始化脚本

==再创建完schema后，切换对应路径再继续执行之后的sql语句，或者ai帮助完成==

连接业务数据库后，按以下顺序执行：

```text
design/sql/02_schema.sql
design/sql/03_views.sql
design/sql/04_triggers.sql
design/sql/05_procedures.sql
design/sql/06_seed_data.sql
```

执行完成后，应检查表、视图、存储过程和初始化数据是否已创建成功。

说明：

- 课程表只维护课程基础信息；`Dengjx_MajorCourses13` 维护专业课程计划，包括课程类型、开设年级和开设学期。
- 开课安排关联专业课程计划、班级、教师、学年学期、容量和是否开放。
- 学生表维护入学时间，系统按北京时间计算当前年级和上/下学期。
- 专业表维护毕业所需学分，学生端“我的学分”会展示已修、毕业所需和剩余学分。
- 学生累计学分在 `Dengjx_Students13.djx_TotalCredits13` 中保留验收用缓存，由成绩触发器自动维护；`V_Dengjx_StudentCreditSummary13` 也提供实时计算学分用于核对。
- `Dengjx_EnrollmentSettings13` 保存管理员全局选课开关，关闭后学生不能提交选课。
- `06_seed_data.sql` 中的用户密码字段是演示用 BCrypt 哈希。
- 实际部署后建议通过后端账号管理功能重置用户密码。
- 不要把真实数据库密码、JWT 密钥或其他凭据写入代码仓库。

## 4. 配置后端环境变量

==或者可以临时设置默认值==

后端配置文件位于：

```text
backend/src/main/resources/application.yml
```

后端支持通过环境变量覆盖数据库和 JWT 配置：

| 环境变量 | 必填 | 说明 |
| --- | --- | --- |
| `DB_URL` | 建议必填 | openGauss JDBC 地址 |
| `DB_USERNAME` | 建议必填 | 数据库用户名 |
| `DB_PASSWORD` | 建议必填 | 数据库密码 |
| `JWT_SECRET` | 生产必填 | JWT 签名密钥，生产环境必须使用强随机值 |
| `JWT_EXPIRATION_MINUTES` | 可选 | JWT 过期时间，默认 120 分钟 |

PowerShell 设置示例：

```powershell
$env:DB_URL="jdbc:opengauss://127.0.0.1:8888/dengjxmis13"
$env:DB_USERNAME="你的数据库用户名"
$env:DB_PASSWORD="你的数据库密码"
$env:JWT_SECRET="请替换为足够长的随机密钥"
$env:JWT_EXPIRATION_MINUTES="120"
```

Linux/macOS 设置示例：

```bash
export DB_URL="jdbc:opengauss://127.0.0.1:8888/dengjxmis13"
export DB_USERNAME="你的数据库用户名"
export DB_PASSWORD="你的数据库密码"
export JWT_SECRET="请替换为足够长的随机密钥"
export JWT_EXPIRATION_MINUTES="120"
```

## 5. 启动后端

使用idea配置好后直接启动即可

## 6. 启动前端

新开一个终端，回到项目根目录后进入前端目录：

```powershell
cd frontend
```

安装依赖：

```powershell
npm install
```

启动开发服务：

```powershell
npm run dev
```

Vite 会在终端输出访问地址，通常是：

```text
http://localhost:5173
```

## 7. 本地联调顺序

推荐按以下顺序启动：

1. 启动 openGauss，并确认数据库可连接。
2. 按 `design/sql` 顺序完成数据库初始化。
3. 设置后端环境变量。
4. 在 `backend` 目录运行 `mvn spring-boot:run`。
5. 在 `frontend` 目录运行 `npm install`。
6. 在 `frontend` 目录运行 `npm run dev`。
7. 浏览器打开 Vite 输出的地址。
8. 使用初始化数据中的账号登录，或通过后端账号管理功能创建/重置账号后登录。

## 8. 目录说明

```text
.
├── backend                 # Spring Boot 后端
│   ├── pom.xml             # Maven 配置
│   └── src
├── frontend                # Vue + Vite 前端
│   ├── package.json        # 前端依赖和脚本
│   ├── vite.config.ts      # Vite 配置，含开发代理
│   └── src
├── design
│   ├── docs                # 接口清单、验收说明
│   └── sql                 # 数据库初始化脚本
├── docs                    # 项目过程文档
└── readme.md               # 项目部署启动说明
```

## 9. 关键启动命令汇总

后端：

```powershell
cd backend
mvn spring-boot:run
```

前端：

```powershell
cd frontend
npm install
npm run dev
```

构建：

```powershell
cd backend
mvn clean package

cd ..\frontend
npm run build
```

## 10. 测试账号

用户名和密码登录

执行 `design/sql/06_seed_data.sql` 初始化数据后，可以使用以下测试账号登录，所有账号密码均为：

```text
123456
```

### 管理员账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | `admin` | `123456` |

### 教师账号

教师账号由教师工号转小写生成。

| 角色 | 教师姓名 | 用户名 | 密码 |
| --- | --- | --- | --- |
| 教师 | 张建国 | `t2023001` | `123456` |
| 教师 | 李芳 | `t2023002` | `123456` |
| 教师 | 王磊 | `t2023003` | `123456` |
| 教师 | 陈静 | `t2023004` | `123456` |
| 教师 | 刘洋 | `t2023005` | `123456` |
| 教师 | 赵文博 | `t2023006` | `123456` |
| 教师 | 胡敏 | `t2023007` | `123456` |
| 教师 | 马强 | `t2023008` | `123456` |

### 学生账号

学生账号由 `s` 加学号生成。

| 角色 | 学生姓名 | 用户名 | 密码 |
| --- | --- | --- | --- |
| 学生 | 刘晨 | `s20230001` | `123456` |
| 学生 | 李雪 | `s20230002` | `123456` |
| 学生 | 王浩 | `s20230003` | `123456` |
| 学生 | 赵敏 | `s20230004` | `123456` |
| 学生 | 陈杰 | `s20230005` | `123456` |
| 学生 | 周怡 | `s20230006` | `123456` |
| 学生 | 孙磊 | `s20230007` | `123456` |
| 学生 | 吴倩 | `s20230008` | `123456` |
| 学生 | 郑宇 | `s20230009` | `123456` |
| 学生 | 何琳 | `s20230010` | `123456` |
| 学生 | 郭强 | `s20230011` | `123456` |
| 学生 | 唐静 | `s20230012` | `123456` |
| 学生 | 邓超 | `s20230013` | `123456` |
| 学生 | 林悦 | `s20230014` | `123456` |
| 学生 | 马骏 | `s20230015` | `123456` |
| 学生 | 高雅 | `s20230016` | `123456` |
| 学生 | 罗成 | `s20230017` | `123456` |
| 学生 | 许宁 | `s20230018` | `123456` |
| 学生 | 程远 | `s20230019` | `123456` |
| 学生 | 姚瑶 | `s20230020` | `123456` |
| 学生 | 蔡明 | `s20230021` | `123456` |
| 学生 | 宋佳 | `s20230022` | `123456` |
| 学生 | 袁博 | `s20230023` | `123456` |
| 学生 | 韩梅 | `s20230024` | `123456` |
