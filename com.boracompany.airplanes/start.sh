#!/bin/bash
set -e

# Usage: ./start.sh

cd "$(dirname "$0")"

# Define the expected JAR path pattern
JAR_FILE=$(ls target/airplanes-*-jar-with-dependencies.jar)

# If the JAR doesn't exist, build it with Maven
if [ -z "$JAR_FILE" ]; then
  echo " JAR file not found. Building project"
  mvn clean package -DskipTests
  JAR_FILE=$(ls target/airplanes-*-jar-with-dependencies.jar)

  if [ -z "$JAR_FILE" ]; then
    echo " Build failed or JAR not found after build."
    exit 1
  fi
else
  echo "Found existing JAR: $JAR_FILE"
fi

# Export DISPLAY variable from the host environment
export DISPLAY=${DISPLAY:-:0}

# Grant access to the X server
xhost +local:docker > /dev/null

# Run docker-compose
echo "Starting Docker Compose..."
docker compose up --abort-on-container-exit --exit-code-from java_app

