JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)
MVN := ../mvnw

.PHONY: run build test verify v clean tidy

run:
	${MVN} spring-boot:run

test verify v:
	${MVN} clean verify

build:
	${MVN} clean package

clean:
	rm -rf target/

tidy:
	${MVN} formatter:format
