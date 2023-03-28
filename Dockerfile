FROM openjdk
WORKDIR shortcut
ADD target/shortcut-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT java -jar app.jar
