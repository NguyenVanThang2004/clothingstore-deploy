# Stage 1: Build Angular app
FROM node:20-alpine AS build
WORKDIR /app

# copy package.json và package-lock.json
COPY package*.json ./
RUN npm ci
RUN npm install -g @angular/cli

# copy toàn bộ source
COPY . .

# build Angular production
RUN npm run build -- --configuration production

# Stage 2: Nginx serve Angular
FROM nginx:1.23.3

# copy file config nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# copy build Angular (Angular v16 không có /browser)
COPY --from=build /app/dist/frontend  /usr/share/nginx/html

EXPOSE 80
