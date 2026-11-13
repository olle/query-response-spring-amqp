JAVA_HOME := $(or $(shell unset JAVA_HOME; /usr/libexec/java_home -v 25 2>/dev/null),$(JAVA_HOME))
export JAVA_HOME

.PHONY: start
start:
	MAVEN_OPTS="-Xms25m -Xmx50m"; \
    mvn spring-boot:run

