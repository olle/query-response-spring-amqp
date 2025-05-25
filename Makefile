JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

.PHONY: verify v
verify v:
	./mvnw verify

.PHONY: install i
install i:
	./mvnw install

.PHONY: tidy format pretty f
tidy pretty format f:
	./mvnw formatter:format

.PHONY: docs watch-docs
docs:
	npm run docs:build
	mvn javadoc:javadoc
	cp -R target/site/apidocs xdocs/.vitepress/dist/
	npm run deploy-docs

watch-docs:
	mvn asciidoctor:http

.PHONY: ui
ui: ui/query-response-ui.jar

ui/query-response-ui.jar: ui/target/query-response-ui.jar
	@cp $< $@

ui/target/query-response-ui.jar: install
	@$(MAKE) -C ui build

.PHONY: up
up:
	docker compose up -d

.PHONY: down
down:
	docker compose down --remove-orphans

.PHONY: repo-clean
repo-clean:
	rm -rf ~/.m2/repository/com/studiomediatech/query-response-spring-amqp

##
## Demo targets
##
## These targets are used to run the demo application and its components.
## They depend on the `install` target to ensure that the application is built
## and ready to run. The up target starts the necessary Docker containers,
## and the run-demo target orchestrates the execution of the demo components.
## Please note that the parallel execution of the demo components is what makes
## it possible to start the individual components in the background.
##
.PHONY: demo run-demo run-demo run-query run-response run-ui
demo: install up
	${MAKE} -j3 run-demo

run-demo: run-ui run-query run-response

run-query:
	sleep 10
	${MAKE} -C examples/querying/

run-response:
	sleep 10
	${MAKE} -C examples/responding/

run-ui:
	${MAKE} demo -C ui/
