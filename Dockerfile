FROM openjdk:17
MAINTAINER protege.stanford.edu

EXPOSE 7761
ARG JAR_FILE
COPY target/${JAR_FILE} webprotege-user-management.jar
ENTRYPOINT ["java","--add-opens=java.management/sun.net=ALL-UNNAMED","-jar","/webprotege-user-management.jar"]