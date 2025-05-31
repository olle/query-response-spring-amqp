JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

.PHONY: start
start:
	MAVEN_OPTS="-Xms10m -Xmx50m"; \
    SPRING_APPLICATION_NAME=myapp mvn clean spring-boot:run

