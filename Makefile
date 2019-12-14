.PHONY: verify

verify:
	docker-compose up -d rabbitmq
	sleep 3 ; \
	  mvn verify ;\
		docker-compose down
