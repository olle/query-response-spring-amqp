JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

.PHONY: verify v
verify v:
	./mvnw verify

.PHONY: install i
install i:
	./mvnw install

.PHONY: all a
all a: verify install
	$(MAKE) -C ui build
	
.PHONY: tidy format pretty f
tidy pretty format f:
	./mvnw formatter:format

##
## Build the documentation and deploy it to the gh-pages branch. The
## documentation is built using VitePress for the main site and Maven Javadoc
## for the API documentation. Local development of the documentation can be done
## using `make docs-dev`, which will start a local VitePress server.
##
.PHONY: docs docs-dev
docs:
	npm run docs:build
	mvn javadoc:javadoc
	cp -R target/site/apidocs docs/.vitepress/dist/
	npm run deploy-docs

docs-dev:
	npm run docs:dev

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

.PHONY: demo demo-s run-demo run-demo-s
demo: install up
	${MAKE} -j3 run-demo

demo-s: install up
	${MAKE} -j2 run-demo-s

.PHONY: run-demo run-demo-s run-query run-response run-ui
run-demo: run-ui run-query run-response

run-demo-s: run-query run-response

run-query:
	sleep 10
	${MAKE} -C examples/querying/

run-response:
	sleep 10
	${MAKE} -C examples/responding/

run-ui:
	${MAKE} -C ui/
