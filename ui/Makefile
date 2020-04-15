.PHONY: run dev dist clean

run: node_modules
	npm run build
	mvn spring-boot:run

dev: node_modules
	npm run watch

dist: node_modules
	npm run build

node_modules:
	npm install

clean:
	rm -rf target/
	rm -rf node_modules/
