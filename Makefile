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

.PHONY: demo run-demo
demo: install up
	${MAKE} -j4 run-demo

.PHONY: run-demo run-query run-response run-ui start-ui
run-demo: run-ui start-ui run-query run-response

run-query:
	sleep 10
	${MAKE} -C examples/querying/

run-response:
	sleep 10
	${MAKE} -C examples/responding/

run-ui:
	${MAKE} -C ui/

start-ui:
	${MAKE} -C ui-frontend/
