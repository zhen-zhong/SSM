# 依旧使用防 403/404 的代理源
FROM dockerpull.com/library/nginx:alpine

# 清理 Nginx 默认页面
RUN rm -rf /usr/share/nginx/html/*

# 加上 soybean-admin/ 前缀，让 Docker 在根上下文中能找到它！
COPY soybean-admin/dist/ /usr/share/nginx/html/

# 把同级目录的 nginx 配置打进镜像里
COPY nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
