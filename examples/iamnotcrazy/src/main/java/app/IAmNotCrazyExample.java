package app;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;


@SpringBootApplication
@EnableRabbit
class IAmNotCrazyExample implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        System.out.println("> Started, will register for stats queries. Press CTRL-C to exit.");
        Thread.currentThread().join();
    }


    @Autowired
    void setup(GenericApplicationContext ctx, final RabbitAdmin admin) {

        System.out.println("> This is a start...");

        AnonymousQueue queue = new AnonymousQueue();
        ctx.registerBean(Queue.class, () -> queue);

        admin.declareQueue(queue);
    }


    @Bean
    RabbitAdmin admin(ConnectionFactory connectionFactory) {

        return new RabbitAdmin(connectionFactory);
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(IAmNotCrazyExample.class).web(WebApplicationType.NONE).run(args);
    }
}
