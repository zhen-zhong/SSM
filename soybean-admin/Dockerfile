FROM nginx:alpine

# 直接将 Jenkins 刚刚打包出来的 dist 复制到 nginx
COPY dist /usr/share/nginx/html

EXPOSE 80 443
CMD["nginx", "-g", "daemon off;"]
