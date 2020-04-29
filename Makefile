.PHONY: verify
verify:
	mvn verify

.PHONY: install
install:
	mvn install

.PHONY: docs
docs:
	mvn asciidoctor:process-asciidoc
