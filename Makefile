.PHONY: verify
verify:
	mvn verify

.PHONY: install
install:
	mvn install

.PHONY: docs watch-docs
docs:
	mvn asciidoctor:process-asciidoc
watch-docs:
	mvn asciidoctor:http
