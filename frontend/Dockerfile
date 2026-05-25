# Etapa 1: build de Angular
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# Etapa 2: servir el build estático con nginx
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Railway inyecta PORT; sustituirlo en nginx.conf al arrancar
CMD ["/bin/sh", "-c", "sed -i \"s/listen 80;/listen ${PORT:-80};/g\" /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
