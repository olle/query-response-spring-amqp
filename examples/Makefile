JAVA_HOME := $(or $(shell unset JAVA_HOME; /usr/libexec/java_home -v 25 2>/dev/null),$(JAVA_HOME))
export JAVA_HOME

.PHONY: start
start:
	docker-compose up

.PHONY: up
up:
	docker-compose up -d

.PHONY: down
down:
	docker-compose down -v --remove-orphans

