FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/simtwop-web.jar /simtwop-web/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/simtwop-web/app.jar"]
