JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

.PHONY: start
start:
	MAVEN_OPTS="-Xms25m -Xmx50m"; \
    mvn spring-boot:run

