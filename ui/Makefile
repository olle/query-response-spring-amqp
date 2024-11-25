JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

.PHONY: run build test verify clean tidy

run:
	mvn spring-boot:run

test verify:
	mvn clean verify

build:
	mvn clean package

clean:
	rm -rf target/

tidy:
	mvn formatter:format
