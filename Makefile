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
	@cp -r target/site/apidocs target/generated-docs/apidocs
	npm run deploy-docs

watch-docs:
	mvn asciidoctor:http

.PHONY: ui
ui: ui/query-response-ui.jar

ui/query-response-ui.jar: ui/target/query-response-ui.jar
	@cp $< $@

ui/target/query-response-ui.jar: install
	@$(MAKE) -C ui dist
