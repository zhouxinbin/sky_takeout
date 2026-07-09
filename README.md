# Sky Takeout（苍穹外卖）

外卖点餐平台，Spring Boot + Vue.js 全栈项目。

## 运行环境

| 依赖 | 版本/说明 |
|------|-----------|
| JDK | 1.8+（推荐 JDK 8） |
| Maven | 3.x |
| MySQL | 5.7+ / 8.x，数据库名 `sky_take_out` |
| Redis | 默认端口 6379 |
| Nginx | 1.x（前端静态服务 + 反向代理） |

## 项目结构

```
sky_takeout/
├── back_end/          # 后端（Spring Boot 多模块 Maven 项目）
│   ├── sky-common/    # 公共工具、异常、属性配置
│   ├── sky-pojo/      # 实体类、DTO、VO
│   └── sky-server/    # 主应用（Controller、Service、Mapper）
├── front_end/         # 前端（Vue.js 打包后的静态文件）
├── nginx.conf         # Nginx 配置模板
└── README.md
```

## 快速启动

### 1. 数据库配置

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

### 2. 导入表结构

将课程提供的 SQL 脚本导入 `sky_take_out` 数据库，创建所需的表。

### 3. 启动后端

```bash
cd back_end
mvn clean install -DskipTests
mvn spring-boot:run -pl sky-server
```

或在 IntelliJ IDEA 中以 `back_end/pom.xml` 作为项目打开，运行 `SkyApplication` 的 `main` 方法。

启动成功后可访问：
- API 服务：`http://localhost:8080`
- Swagger 文档：`http://localhost:8080/doc.html`

### 4. 配置 Nginx

将仓库中的 `nginx.conf` 复制到 nginx 的 servers 目录：

```bash
# macOS (Homebrew)
cp nginx.conf /opt/homebrew/etc/nginx/servers/sky_takeout.conf

# Linux
cp nginx.conf /etc/nginx/conf.d/sky_takeout.conf
```

修改 nginx.conf 中的 `root` 路径，指向本项目下的 `front_end` 目录：

```nginx
root   /实际路径/sky_takeout/front_end;
```

测试并重载 nginx：

```bash
nginx -t
nginx -s reload
```

### 5. 访问前端

打开浏览器访问 `http://localhost`（默认 80 端口），即可进入管理系统登录页面。

### Nginx 工作原理

```
浏览器 :80
    │
    ▼
  Nginx ── /、/*.js、*.css ──▶ front_end/ 静态文件
    │
    ├── /api/xxx  ──▶ proxy ──▶ http://localhost:8080/admin/xxx  (管理端)
    ├── /user/xxx ──▶ proxy ──▶ http://localhost:8080/user/xxx  (用户端)
    └── /ws/xxx   ──▶ proxy ──▶ http://localhost:8080/ws/xxx   (WebSocket)
```
