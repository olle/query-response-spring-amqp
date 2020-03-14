package com.studiomediatech.queryresponse;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Enables support for Query/Response with AMQP or RabbitMQ as the shared broker. Configures all the required
 * components for a Query/Response client, ensuring the necessary AMQP resources are declared, as well as a
 * initializing the {@link QueryRegistry} and a {@link ResponseRegistry} beans.
 *
 * <p>Easy to use on Spring and Spring Boot applications, simply by adding it in the application starter class like
 * this:</p>
 *
 * <pre>
   {@literal @}SpringBootApplication
   {@literal @}EnableQueryResponse
   public class MyApplication {
       // ...
   }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(QueryResponseConfiguration.class)
public @interface EnableQueryResponse {

    // OK
}
