FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/kaiden-player.jar /kaiden-player/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/kaiden-player/app.jar"]
