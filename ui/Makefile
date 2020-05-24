.PHONY: run dev dist clean test lint

#
# Starting watch for client-side, or frontend assets, and running the backend
# server for e2e integration.
#
run: node_modules
	mvn clean
	npm run watch & mvn spring-boot:run

#
# Focused on client-side, this target will run a front-end only server, so
# no backend service.
#
dev: node_modules
	npm run serve

#
# This target is issued by the Maven `package` goal. Used when building the
# distribution JAR file.
#
dist: node_modules
	npm run build

node_modules:
	npm install

test:
	npm run test

lint:
	npm run lint

clean:
	rm -rf target/
	rm -rf node_modules/
