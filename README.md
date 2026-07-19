# Sky Takeout（苍穹外卖）

外卖点餐平台，Spring Boot + Vue.js 全栈项目。

## 项目结构

```
sky_takeout/
├── back_end/          # 后端（Spring Boot 多模块 Maven 项目）
│   ├── sky-common/    # 公共工具、异常、属性配置
│   ├── sky-pojo/      # 实体类、DTO、VO
│   └── sky-server/    # 主应用（Controller、Service、Mapper）
├── front_end/         # 前端源码（Vue 2 + TypeScript），dist/ 为编译产物
├── nginx.conf         # Nginx 配置模板
└── README.md
```

---

## 后端

### 运行环境

| 依赖 | 版本/说明 |
|------|-----------|
| JDK | 1.8+（推荐 JDK 8） |
| Maven | 3.x |
| MySQL | 5.7+ / 8.x，数据库名 `sky_take_out` |
| Redis | 默认端口 6379 |
| 阿里云 OSS | 文件存储（图片上传等） |

### 数据库配置

确保 MySQL 已运行，创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS sky_take_out;
```

修改 `back_end/sky-server/src/main/resources/application-dev.yml` 中的数据库连接信息（用户名、密码）和 Redis 配置：

```yaml
spring:
  redis:
    host: localhost
    port: 6379

sky:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: sky_take_out
    username: root
    password: your_password
```

将课程提供的 SQL 脚本导入 `sky_take_out` 数据库，创建所需的表。

### 阿里云 OSS 配置

在 `application-dev.yml` 中配置以下 OSS 信息（用于文件上传）：

```yaml
sky:
  alioss:
    access-key-id: your_access_key_id
    access-key-secret: your_access_key_secret
    bucket-name: your_bucket_name
    endpoint: oss-cn-xxx.aliyuncs.com
```

### 启动后端

```bash
cd back_end
mvn clean install -DskipTests
mvn spring-boot:run -pl sky-server
```

或在 IntelliJ IDEA 中以 `back_end/pom.xml` 作为项目打开，运行 `SkyApplication` 的 `main` 方法。

启动成功后可访问：
- API 服务：`http://localhost:8080`
- Swagger 文档：`http://localhost:8080/doc.html`

---

## 前端

### 运行环境

| 依赖 | 版本/说明 |
|------|-----------|
| Node.js | 推荐 14.x ~ 16.x（26.x 也可运行，见下方注意事项） |
| Yarn | 1.22+（项目使用 Yarn 管理依赖，不能用 npm） |

### 技术栈

| 类别 | 详情 |
|------|------|
| 框架 | Vue 2 + TypeScript（vue-class-component / vue-property-decorator） |
| UI 组件库 | Element UI 2.x |
| 构建工具 | Vue CLI 3（webpack 4） |
| 样式 | SCSS（sass + sass-loader 7.x） |
| 图表 | ECharts 5 |
| 路由 | vue-router 3 |
| 状态管理 | Vuex 3 + vuex-persistedstate |
| HTTP 请求 | axios |
| 测试 | Jest（单元）+ Cypress（e2e） |
| 编译产物 | `front_end/dist/`，Nginx root 指向此处 |

### 安装依赖

```bash
cd front_end

# 首次或新增依赖时执行（跳过 Cypress 二进制和原生模块编译）
CYPRESS_INSTALL_BINARY=0 yarn install --ignore-scripts
```

### 编译

```bash
cd front_end

yarn build
```

编译产出在 `front_end/dist/`，Nginx 需指向该目录。

### Node.js 26.x 用户

如果使用 Node.js 26.x（及以上），每次编译需额外传递环境变量：

```bash
NODE_OPTIONS="--dns-result-order=ipv4first --openssl-legacy-provider" yarn build
```

可在项目目录下创建 `.npmrc` 免去每次手写：

```bash
echo 'node-options="--dns-result-order=ipv4first --openssl-legacy-provider"' > .npmrc
```

| 选项 | 作用 |
|------|------|
| `--dns-result-order=ipv4first` | 修复 Node.js 17+ 默认 IPv6 解析导致 npm/yarn 网络超时 |
| `--openssl-legacy-provider` | 修复 webpack 4 在 Node.js 17+ 的 OpenSSL 兼容问题 |

---

## Nginx

### 部署配置

将仓库中的 `nginx.conf` 复制到 nginx 的 servers 目录：

```bash
# macOS (Homebrew)
cp nginx.conf /opt/homebrew/etc/nginx/servers/sky_takeout.conf

# Linux
cp nginx.conf /etc/nginx/conf.d/sky_takeout.conf
```

修改 nginx.conf 中的 `root` 路径，指向本项目下的 `front_end/dist` 目录：

```nginx
root   /实际路径/sky_takeout/front_end/dist;
```

测试并重载 nginx：

```bash
nginx -t
nginx -s reload
```

### 工作原理

```
浏览器 :80
    │
    ▼
  Nginx ── /、/*.js、*.css ──▶ front_end/dist/ 静态文件
    │
    ├── /api/xxx  ──▶ proxy ──▶ http://localhost:8080/admin/xxx  (管理端)
    ├── /user/xxx ──▶ proxy ──▶ http://localhost:8080/user/xxx  (用户端)
    └── /ws/xxx   ──▶ proxy ──▶ http://localhost:8080/ws/xxx   (WebSocket)
```

### 访问前端

打开浏览器访问 `http://localhost`（默认 80 端口），即可进入管理系统登录页面。
