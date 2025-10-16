# GIAI ĐOẠN 1: BUILD
# Sử dụng image Gradle với OpenJDK 21 để build.
FROM gradle:8.4.0-jdk21 AS build

# Đặt thư mục làm việc bên trong container
WORKDIR /app

# Copy các file cấu hình Gradle và wrapper để tận dụng cache
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Tải các dependency trước (optional nhưng tốt cho cache)
# Chạy một tác vụ nhẹ nhàng để gradle tải libs về
RUN ./gradlew --build-cache dependencies || true

# Copy toàn bộ source code
COPY src ./src

# Build ứng dụng thành file .jar, bỏ qua tests (-x test)
RUN ./gradlew build -x test

# GIAI ĐOẠN 2: RUN
# Sử dụng một image Java 21 Runtime nhỏ gọn để chạy ứng dụng
FROM openjdk:21-jre-slim

WORKDIR /app

# Copy file .jar đã được build từ giai đoạn 1
# Chú ý: Gradle build ra thư mục /build/libs/
COPY --from=build /app/build/libs/*.jar app.jar

# Port mà ứng dụng Spring Boot của bạn sẽ chạy
EXPOSE 8082

# Lệnh để khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]