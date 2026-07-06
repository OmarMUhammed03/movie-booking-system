# Stage 1: Build the Angular application
# Updated to Node 22 to satisfy Angular CLI requirements
FROM node:22-alpine AS build

WORKDIR /app

# Copy package.json and package-lock.json first
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy the rest of the application code
COPY . .

# Build the app for production
RUN npm run build -- --configuration=production

# Stage 2: Serve the application with Nginx
FROM nginx:stable-alpine

# Remove default Nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# UPDATED PATH: Based on your log 'ticket-reservation@0.0.0 build'
# Angular 17+ builds to dist/[project-name]/browser
COPY --from=build /app/dist/ticket-reservation/browser /usr/share/nginx/html

# Copy your custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]