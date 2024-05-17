FROM openjdk:21-oracle
COPY target/order-service-1.0-SNAPSHOT-fat.jar /usr/lib/contexts/order-service.jar
CMD java ${JAVA_OPTS} -jar /usr/lib/contexts/order-service.jar
