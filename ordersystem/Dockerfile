# 첫번째 스테이지
FROM openjdk:11 as stage1

WORKDIR /app

# /app/gradlew 파일로 생성
COPY gradlew .
# 실행 권한 부여
RUN chmod +x gradlew

# /app/gradle 폴더로 생성
COPY gradle gradle
COPY src src
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew bootJar

# 두번째 스테이지
FROM openjdk:11
WORKDIR /app
# stage1에 있는 jar를 stage2의 app.jar라는 이름으로 copy
COPY --from=stage1 /app/build/libs/*.jar app.jar

# CMD 또는 ENTRYPOINT를 통해 컨테이너를 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
