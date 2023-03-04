JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 17)

.PHONY: verify
verify:
	mvn verify

.PHONY: install
install:
	mvn install

.PHONY: docs watch-docs
docs:
	mvn asciidoctor:process-asciidoc
	mvn javadoc:javadoc
	cp -R target/site/apidocs target/generated-docs/
	npm run deploy-docs

watch-docs:
	mvn asciidoctor:http

.PHONY: ui
ui: ui/query-response-ui.jar

ui/query-response-ui.jar: ui/target/query-response-ui.jar
	@cp $< $@

ui/target/query-response-ui.jar: install
	@$(MAKE) -C ui dist

.PHONY: up
up:
	docker-compose up -d

.PHONY: down
down:
	docker-compose down --remove-orphans

.PHONY: repo-clean
repo-clean:
	rm -rf ~/.m2/repository/com/studiomediatech/query-response-spring-amqp
