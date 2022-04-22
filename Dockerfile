# build base image
FROM maven:3-openjdk-11-slim as maven

# copy pom.xml
COPY ./pom.xml ./pom.xml

# copy src files
COPY ./src ./src

# build
RUN mvn package

# final base image
FROM openjdk:11-jre-slim

# set deployment directory
WORKDIR /${APP_NAME}

# copy over the built artifact from the maven image
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /GIFMButtonService/target/ROOT.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]

#Settings
ENV SERVER_PORT='9000'
ENV SPRING_SQL_INIT_PLATFORM='h2'
ENV SPRING_DATASOURCE_DRIVERCLASSNAME='org.h2.Driver'
ENV SPRING_DATASOURCE_URL='jdbc:h2:mem:AZ;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE'
ENV SPRING_JPA_DATABASEPLATFORM='org.hibernate.dialect.H2Dialect'
ENV SPRING_JPA_HIBERNATE_DDLAUTO='create-drop'
ENV SPRING_DATASOURCE_USERNAME='spring'
ENV SPRING_DATASOURCE_PASSWORD='spring'
ENV APP_USERNAME='admin'
ENV APP_PASSWORD='admin'

#expose port
EXPOSE ${SERVER_PORT}

#run java command
#CMD java -jar ./EIDER.jar
