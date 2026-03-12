FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

COPY soybean-admin/dist/ /usr/share/nginx/html/

COPY nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
