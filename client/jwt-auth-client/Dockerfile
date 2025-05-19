# Stage 1: Build the React app
FROM node:20 as build

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the React app
COPY . .

# Build the app
RUN npm run build

# Stage 2: Serve with NGINX
FROM nginx:alpine

# Copy built React app from Stage 1
COPY --from=build /app/build /usr/share/nginx/html

# Copy your custom NGINX config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]
