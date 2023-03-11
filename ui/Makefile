JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 17)

.PHONY: run build test verify clean

run:
	mvn spring-boot:run

test verify:
	mvn clean verify

build:
	mvn clean package

clean:
	rm -rf target/
