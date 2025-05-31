JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

.PHONY: start
start:
	MAVEN_OPTS="-Xms30m -Xmx50m"; \
    mvn -U spring-boot:run
