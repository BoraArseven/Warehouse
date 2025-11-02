FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libxinerama1 \
    libxcursor1 \
    libfreetype6 \
    fontconfig \
    fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/airplanes-*-jar-with-dependencies.jar ./app.jar

CMD ["java", "-jar", "app.jar", "--mongo-host=mongodb", "--mongo-port=27017", "--db-name=warehouse", "--db-collection=airplane"]
