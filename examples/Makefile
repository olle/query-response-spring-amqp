JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

.PHONY: start
start:
	docker-compose up

.PHONY: up
up:
	docker-compose up -d

.PHONY: down
down:
	docker-compose down -v --remove-orphans

