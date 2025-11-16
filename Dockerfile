# ----- Stage 1: Build ứng dụng với Gradle Wrapper -----
# Sử dụng image JDK 21
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# --- TỐI ƯU CACHE (1) ---
# Copy các file build trước.
# Nếu các file này không đổi, Docker sẽ dùng cache cho bước RUN tiếp theo.
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# --- TỐI ƯU CACHE (2) ---
# Chạy 'dependencies' để download thư viện.
# Layer này sẽ được cache lại nếu file build không đổi.
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# --- TỐI ƯU CACHE (3) ---
# Chỉ copy code nguồn sau khi đã tải dependencies
# Giờ đây, nếu bạn chỉ đổi code (src), Docker sẽ không cần tải lại thư viện.
COPY src ./src

# Build ứng dụng
RUN ./gradlew build --no-daemon -x test

# ----- Stage 2: Tạo image cuối cùng để chạy -----
# Sử dụng image jre (bản thường theo yêu cầu)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Đặt biến môi trường PORT (giá trị 8080 này chỉ là fallback)
# Cloud Run sẽ ghi đè (override) biến này lúc runtime.
ENV PORT 8080

# --- SỬA LỖI BUILD ---
# Copy file .jar đã được build từ stage 1
# Dùng *[!plain].jar để chắc chắn copy đúng file boot-jar
COPY --from=build /app/build/libs/*[!plain].jar app.jar

# --- SỬA LỖI RUNTIME ---
# Bảo Spring Boot lắng nghe trên $PORT mà Cloud Run cung cấp
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT}"]
