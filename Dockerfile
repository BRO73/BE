# ----- Stage 1: Build ứng dụng với Gradle Wrapper -----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew build --no-daemon -x test

# Xóa file plain.jar để tránh lỗi copy nhiều file
RUN rm -f build/libs/*-plain.jar


# ----- Stage 2: Image chạy -----
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT 8080

# Lúc này thư mục libs chỉ còn 1 file jar → COPY OK
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT}"]
