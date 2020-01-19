.PHONY: start
start:
	$(MAKE) -C .. start &&\
	sleep 3 ;\
	MAVEN_OPTS="-Xms50m -Xmx100m"; \
	    mvn spring-boot:run ;\
		$(MAKE) -C .. stop
