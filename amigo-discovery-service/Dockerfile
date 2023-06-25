FROM java:8
EXPOSE 8761
ADD target/amigo-discovery-service-1.0.0-SNAPSHOT.jar /amigo-discovery-service.jar
ADD runjava.sh /runjava.sh
ENTRYPOINT /runjava.sh /amigo-discovery-service.jar