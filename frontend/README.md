# 前端运行说明

1. 执行 `npm install`。
2. 执行 `npm run dev`。
3. 打开 Vite 输出的本地地址。

## 验证命令

```powershell
npm run build
```

开发环境默认通过 Vite 代理把 `/api` 转发到 `http://localhost:8080`。

## 页面验证

- `src/main.ts` 已注册 Element Plus，登录页表单组件应渲染为可输入、可点击控件。
- 联调登录前需要先启动后端服务，否则登录请求会由 Vite 代理返回连接失败或 502。
