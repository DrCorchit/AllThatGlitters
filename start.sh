#!/usr/bin/env bash
git pull || exit 1
echo "Updated server code; building  server."
sudo mvn clean package || exit 1
sudo cp target/AllThatGlitters-*.jar target/server.jar || exit 1
echo "Generating Files."
sudo java -cp target/server.jar net.allthatglitters.server.Generate
echo "Starting Server."
sudo java -jar target/server.jar $1 &