JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 11)

.PHONY: run build test clean

run:
	mvn spring-boot:run

test:
	mvn clean verify

build:
	mvn clean package

clean:
	rm -rf target/
