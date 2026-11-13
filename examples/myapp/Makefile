JAVA_HOME := $(or $(shell unset JAVA_HOME; /usr/libexec/java_home -v 25 2>/dev/null),$(JAVA_HOME))
export JAVA_HOME

.PHONY: start
start:
	MAVEN_OPTS="-Xms10m -Xmx50m"; \
    SPRING_APPLICATION_NAME=myapp mvn clean spring-boot:run

