# ----- Stage 1: Build ứng dụng với Gradle Wrapper -----
# Sử dụng image JDK 21 (bản slim) để build
FROM eclipse-temurin:21-jdk-slim AS build
WORKDIR /app

# Copy các file cần thiết cho Gradle
# (Sử dụng wrapper của project)
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
# Copy mã nguồn
COPY src ./src

# Cấp quyền thực thi cho wrapper và chạy build
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon -x test

# ----- Stage 2: Tạo image cuối cùng để chạy -----
# Sử dụng image JRE 21 (siêu nhẹ, chỉ để chạy)
FROM openjdk:21-jre-slim
WORKDIR /app

# Copy file .jar đã được build từ stage 1
COPY --from=build /app/build/libs/*.jar app.jar

# Mở port (mặc định Spring Boot là 8080)
EXPOSE 8082

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java","-jar","app.jar"]
