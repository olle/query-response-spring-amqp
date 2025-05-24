JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)
MVN := ../mvnw

.PHONY: run build test verify v clean tidy

run:
	${MVN} spring-boot:run

test verify v:
	${MVN} clean verify

build:
	${MVN} clean prepare-package -DskipTests
	mkdir -p target/classes/static
	@$(MAKE) -C ../ui-frontend build
	cp -r ../ui-frontend/dist/* target/classes/static/
	${MVN} package -DskipTests

clean:
	rm -rf target/

tidy:
	${MVN} formatter:format
