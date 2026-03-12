# 依旧使用防 403/404 的代理源
FROM dockerpull.com/library/nginx:alpine

# 清理 Nginx 默认页面
RUN rm -rf /usr/share/nginx/html/*

# 【修改点 1】复制前端打包产物（加上了 soybean-admin/ 前缀）
COPY soybean-admin/dist/ /usr/share/nginx/html/

# 【修改点 2】把外部同级的 Nginx 配置直接打进镜像里，彻底解决 Jenkins 挂载报错！
COPY nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]