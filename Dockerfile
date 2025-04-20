# Sử dụng image OpenJDK chính thức làm base image
FROM openjdk:23-jdk-slim

# Tạo thư mục làm việc trong container
WORKDIR /app

# Copy file JAR từ máy local vào container
COPY target/doan.jar app.jar

# Mở cổng mà ứng dụng sẽ chạy (mặc định Spring Boot dùng 8082 theo environment.ts của bạn)
EXPOSE 8082

# Lệnh chạy ứng dụng khi container khởi động
CMD ["java", "-jar", "app.jar"]