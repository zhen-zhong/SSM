# 第一阶段：编译打包
FROM node:22.16.0-alpine AS builder
WORKDIR /app
# 安装 pnpm
RUN npm install -g pnpm
# 复制 package.json 和 lock 文件并安装依赖
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
# 复制所有源代码并构建
COPY . .
RUN pnpm run build

# 第二阶段：Nginx 部署运行阶段
FROM nginx:alpine
# 将第一阶段生成的 dist 产物复制到 nginx 默认 HTML 目录
COPY --from=builder /app/dist /usr/share/nginx/html
# (注：Nginx的配置文件我们会通过 docker-compose 映射进去，这里不需要 copy)
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]
