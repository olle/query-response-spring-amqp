Single Response Setup
=====================

The minimal example to reproduce the issue where _anonymous_ queues, for
Query/Response responses, fail to recover after a loss of connection with the
RabbitMQ broker.

## Steps to reproduce

To run this scenario ensure the following:

* Build and install the Query/Response Maven dependency, running `make install`
  from the repository root (the main project).

* Open the `examples/` folder in a terminal console and start the RabbitMQ
  docker broker image, by issuing `make`.

* In another terminal console, go to the `examples/singleresponse/` folder
  (this one), and start start the minimal client by issuing `make`.

Now after the client connects to the broker, which can be inspected in the admin
UI at http://localhost:15672, shut down the RabbitMQ docker broker container, 
by pressing CTRL-C in the first terminal console window. Then restart it again
by running `make`.

## Expected results

It's desirable to have any resources in Query/Response recoverable from failures
as the Spring AMQP container dictates i.e. recover based on the capabilities of
the listener container and the Spring AMQP caching connection factory.

After a re-established connection, the listener should be able to reconnect to
the declared exclusive queue.

## Failure scenario

The minimal scenario, with captured output from the console below, displays
the following:

1. Initial startup and connect. The client is connected and registers a queue
   with a container and listener. All is good.

2. Connection is lost, as the RabbitMQ broker is shutdown - the docker container
   is closed `CTRL-C`.

3. Connection loss is detected on the channel, and a failure is raised. The
   listener container is cancelled.

4. A consumer restart is attempted, it fails as there still is no connection to
   the broker.

5. The RabbitMQ broker recovers, as it is restarted and ready for connections.

6. Another consumer restart is attempted. The connection to the broker recovers.
   The exchange `query-response` recovers.

7.  A channel failure is raised from the cached connection factory, due to
    a missing queue `404` response.

8. The listener container is projected to the failure to reconnect, on behalf of
   the missing queue, and is scheduled for restart (again).

9. Cycling failure begins. A consumer restart is attempted, the channel fails
   due to the missing `404` queue response, and the listener container is
   scheduled for restart. Where stuck on 9.

## Console Output

```shell
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.6.RELEASE)
2020-05-10 12:25:09.089  INFO 90030 --- [           main] uration$$EnhancerBySpringCGLIB$$86a134ee : //> Declared Exchange [name=query-response, type=topic, durable=true, autoDelete=true, internal=false, arguments={}]
> Started, will register for stats queries. Press CTRL-C to exit.
2020-05-10 12:25:12.103 DEBUG 90030 --- [pool-2-thread-1] c.s.queryresponse.Statistics             : Registering response for statistics queries...
2020-05-10 12:25:12.247  INFO 90030 --- [pool-2-thread-1] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
2020-05-10 12:25:12.310  INFO 90030 --- [pool-2-thread-1] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#30f4b1a6:0/SimpleConnection@3dfff469 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 54909]
2020-05-10 12:25:12.312 DEBUG 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitAdmin         : Initializing declarations
2020-05-10 12:25:12.313  INFO 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitAdmin         : Auto-declaring a non-durable or auto-delete Exchange (query-response) durable:true, auto-delete:true. It will be deleted by the broker if it shuts down, and can be redeclared by closing and reopening the connection.
2020-05-10 12:25:12.323 DEBUG 90030 --- [pool-2-thread-1] o.s.a.r.c.CachingConnectionFactory       : Creating cached Rabbit Channel from AMQChannel(amqp://guest@127.0.0.1:5672/,1)
2020-05-10 12:25:12.337 DEBUG 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitTemplate      : Executing callback RabbitAdmin$$Lambda$384/0x0000000800d7dc40 on RabbitMQ Channel: Cached Rabbit Channel: AMQChannel(amqp://guest@127.0.0.1:5672/,1), conn: Proxy@4c819821 Shared Rabbit Connection: SimpleConnection@3dfff469 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 54909]
2020-05-10 12:25:12.337 DEBUG 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitAdmin         : declaring Exchange 'query-response'
2020-05-10 12:25:12.342 DEBUG 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitAdmin         : Declarations finished
2020-05-10 12:25:12.342 DEBUG 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitTemplate      : Executing callback RabbitAdmin$$Lambda$376/0x0000000800d7fc40 on RabbitMQ Channel: Cached Rabbit Channel: AMQChannel(amqp://guest@127.0.0.1:5672/,1), conn: Proxy@4c819821 Shared Rabbit Connection: SimpleConnection@3dfff469 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 54909]
2020-05-10 12:25:12.342 DEBUG 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitAdmin         : declaring Queue '35277c24-fb82-4096-805c-5c12fd3de915'
2020-05-10 12:25:12.347  INFO 90030 --- [pool-2-thread-1] c.s.queryresponse.RabbitFacade           : //> Declared Binding [destination=35277c24-fb82-4096-805c-5c12fd3de915, exchange=query-response, routingKey=query-response/stats, arguments={}]
2020-05-10 12:25:12.348 DEBUG 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitTemplate      : Executing callback RabbitAdmin$$Lambda$385/0x0000000800d7d040 on RabbitMQ Channel: Cached Rabbit Channel: AMQChannel(amqp://guest@127.0.0.1:5672/,1), conn: Proxy@4c819821 Shared Rabbit Connection: SimpleConnection@3dfff469 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 54909]
2020-05-10 12:25:12.348 DEBUG 90030 --- [pool-2-thread-1] o.s.amqp.rabbit.core.RabbitAdmin         : Binding destination [35277c24-fb82-4096-805c-5c12fd3de915 (QUEUE)] to exchange [query-response] with routing key [query-response/stats]
2020-05-10 12:25:12.361 DEBUG 90030 --- [pool-2-thread-1] o.s.a.r.l.DirectMessageListenerContainer : Starting Rabbit listener container.
2020-05-10 12:25:12.365  INFO 90030 --- [pool-2-thread-1] o.s.a.r.l.DirectMessageListenerContainer : Container initialized for queues: [35277c24-fb82-4096-805c-5c12fd3de915]
2020-05-10 12:25:12.365  INFO 90030 --- [pool-2-thread-1] c.s.queryresponse.ResponseRegistry       : Registered response Response [query='query-response/stats', queue=35277c24-fb82-4096-805c-5c12fd3de915]
2020-05-10 12:25:12.374 DEBUG 90030 --- [pool-1-thread-3] o.s.a.r.l.DirectMessageListenerContainer : New SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=amq.ctag-eH25V0hbMHo5yjAyz5WHcQ identity=41f67ed6] consumeOk
2020-05-10 12:25:12.374  INFO 90030 --- [cTaskExecutor-1] o.s.a.r.l.DirectMessageListenerContainer : SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=amq.ctag-eH25V0hbMHo5yjAyz5WHcQ identity=41f67ed6] started
2020-05-10 12:25:15.948 ERROR 90030 --- [ 127.0.0.1:5672] o.s.a.r.c.CachingConnectionFactory       : Channel shutdown: connection error
2020-05-10 12:25:17.376 ERROR 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Consumer canceled - channel closed SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=amq.ctag-eH25V0hbMHo5yjAyz5WHcQ identity=41f67ed6]
2020-05-10 12:25:17.376 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Closing cached Channel: AMQChannel(amqp://guest@127.0.0.1:5672/,1)
2020-05-10 12:25:17.377 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Attempting to restart consumer SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=amq.ctag-eH25V0hbMHo5yjAyz5WHcQ identity=41f67ed6]
2020-05-10 12:25:17.378  INFO 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
2020-05-10 12:25:17.386  WARN 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : basicConsume failed, scheduling consumer for queue 35277c24-fb82-4096-805c-5c12fd3de915 for restart

org.springframework.amqp.AmqpConnectException: java.net.ConnectException: Connection refused
	at org.springframework.amqp.rabbit.support.RabbitExceptionTranslator.convertRabbitAccessException(RabbitExceptionTranslator.java:61) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:510) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createConnection(CachingConnectionFactory.java:751) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createBareChannel(CachingConnectionFactory.java:702) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.getCachedChannelProxy(CachingConnectionFactory.java:676) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.getChannel(CachingConnectionFactory.java:567) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.access$1600(CachingConnectionFactory.java:102) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory$ChannelCachingConnectionProxy.createChannel(CachingConnectionFactory.java:1430) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.consume(DirectMessageListenerContainer.java:702) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.doConsumeFromQueue(DirectMessageListenerContainer.java:681) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.restartConsumer(DirectMessageListenerContainer.java:523) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.lambda$startMonitor$2(DirectMessageListenerContainer.java:453) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54) ~[spring-context-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515) ~[na:na]
	at java.base/java.util.concurrent.FutureTask.runAndReset(FutureTask.java:305) ~[na:na]
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:305) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628) ~[na:na]
	at java.base/java.lang.Thread.run(Thread.java:830) ~[na:na]
Caused by: java.net.ConnectException: Connection refused
	at java.base/sun.nio.ch.Net.pollConnect(Native Method) ~[na:na]
	at java.base/sun.nio.ch.Net.pollConnectNow(Net.java:579) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl.timedFinishConnect(NioSocketImpl.java:549) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl.connect(NioSocketImpl.java:597) ~[na:na]
	at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:339) ~[na:na]
	at java.base/java.net.Socket.connect(Socket.java:603) ~[na:na]
	at com.rabbitmq.client.impl.SocketFrameHandlerFactory.create(SocketFrameHandlerFactory.java:60) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1113) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1063) ~[amqp-client-5.7.3.jar:5.7.3]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:526) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:473) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	... 17 common frames omitted

2020-05-10 12:25:27.367 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Attempting to restart consumer SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=null identity=442d909]
2020-05-10 12:25:27.367  INFO 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
2020-05-10 12:25:27.381 ERROR 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Cannot connect to server

org.springframework.amqp.AmqpConnectException: org.springframework.amqp.AmqpIOException: java.io.IOException
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.doConsumeFromQueue(DirectMessageListenerContainer.java:672) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.restartConsumer(DirectMessageListenerContainer.java:523) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.lambda$startMonitor$2(DirectMessageListenerContainer.java:453) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54) ~[spring-context-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515) ~[na:na]
	at java.base/java.util.concurrent.FutureTask.runAndReset(FutureTask.java:305) ~[na:na]
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:305) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628) ~[na:na]
	at java.base/java.lang.Thread.run(Thread.java:830) ~[na:na]
Caused by: org.springframework.amqp.AmqpIOException: java.io.IOException
	at org.springframework.amqp.rabbit.support.RabbitExceptionTranslator.convertRabbitAccessException(RabbitExceptionTranslator.java:70) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:510) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createConnection(CachingConnectionFactory.java:751) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.doConsumeFromQueue(DirectMessageListenerContainer.java:667) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	... 9 common frames omitted
Caused by: java.io.IOException: null
	at com.rabbitmq.client.impl.AMQChannel.wrap(AMQChannel.java:129) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.wrap(AMQChannel.java:125) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection.start(AMQConnection.java:375) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1115) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1063) ~[amqp-client-5.7.3.jar:5.7.3]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:526) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:473) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	... 11 common frames omitted
Caused by: com.rabbitmq.client.ShutdownSignalException: connection error
	at com.rabbitmq.utility.ValueOrException.getValue(ValueOrException.java:66) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.utility.BlockingValueOrException.uninterruptibleGetValue(BlockingValueOrException.java:36) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel$BlockingRpcContinuation.getReply(AMQChannel.java:502) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection.start(AMQConnection.java:317) ~[amqp-client-5.7.3.jar:5.7.3]
	... 15 common frames omitted
Caused by: java.io.EOFException: null
	at java.base/java.io.DataInputStream.readUnsignedByte(DataInputStream.java:295) ~[na:na]
	at com.rabbitmq.client.impl.Frame.readFrom(Frame.java:91) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.SocketFrameHandler.readFrame(SocketFrameHandler.java:184) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:598) ~[amqp-client-5.7.3.jar:5.7.3]
	... 1 common frames omitted

2020-05-10 12:25:32.368 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Attempting to restart consumer SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=null identity=6e105466]
2020-05-10 12:25:32.368  INFO 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
2020-05-10 12:25:32.395  INFO 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#30f4b1a6:3/SimpleConnection@7f119ecc [delegate=amqp://guest@127.0.0.1:5672/, localPort= 54922]
2020-05-10 12:25:32.395 DEBUG 90030 --- [nsumerMonitor-1] o.s.amqp.rabbit.core.RabbitAdmin         : Initializing declarations
2020-05-10 12:25:32.396  INFO 90030 --- [nsumerMonitor-1] o.s.amqp.rabbit.core.RabbitAdmin         : Auto-declaring a non-durable or auto-delete Exchange (query-response) durable:true, auto-delete:true. It will be deleted by the broker if it shuts down, and can be redeclared by closing and reopening the connection.
2020-05-10 12:25:32.404 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Creating cached Rabbit Channel from AMQChannel(amqp://guest@127.0.0.1:5672/,1)
2020-05-10 12:25:32.404 DEBUG 90030 --- [nsumerMonitor-1] o.s.amqp.rabbit.core.RabbitTemplate      : Executing callback RabbitAdmin$$Lambda$384/0x0000000800d7dc40 on RabbitMQ Channel: Cached Rabbit Channel: AMQChannel(amqp://guest@127.0.0.1:5672/,1), conn: Proxy@4c819821 Shared Rabbit Connection: SimpleConnection@7f119ecc [delegate=amqp://guest@127.0.0.1:5672/, localPort= 54922]
2020-05-10 12:25:32.404 DEBUG 90030 --- [nsumerMonitor-1] o.s.amqp.rabbit.core.RabbitAdmin         : declaring Exchange 'query-response'
2020-05-10 12:25:32.409 DEBUG 90030 --- [nsumerMonitor-1] o.s.amqp.rabbit.core.RabbitAdmin         : Declarations finished
2020-05-10 12:25:32.417 DEBUG 90030 --- [ 127.0.0.1:5672] o.s.a.r.c.CachingConnectionFactory       : Channel shutdown: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/', class-id=50, method-id=10)
2020-05-10 12:25:32.417 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Detected closed channel on exception.  Re-initializing: AMQChannel(amqp://guest@127.0.0.1:5672/,1)
2020-05-10 12:25:32.421 ERROR 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Queue not present, scheduling consumer SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=null identity=23f761b8] for restart

java.io.IOException: null
	at com.rabbitmq.client.impl.AMQChannel.wrap(AMQChannel.java:129) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.wrap(AMQChannel.java:125) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.exnWrappingRpc(AMQChannel.java:147) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.ChannelN.queueDeclarePassive(ChannelN.java:1012) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.ChannelN.queueDeclarePassive(ChannelN.java:52) ~[amqp-client-5.7.3.jar:5.7.3]
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:na]
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:na]
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:na]
	at java.base/java.lang.reflect.Method.invoke(Method.java:567) ~[na:na]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory$CachedChannelInvocationHandler.invoke(CachingConnectionFactory.java:1190) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at com.sun.proxy.$Proxy48.queueDeclarePassive(Unknown Source) ~[na:na]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.consume(DirectMessageListenerContainer.java:705) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.doConsumeFromQueue(DirectMessageListenerContainer.java:681) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.restartConsumer(DirectMessageListenerContainer.java:523) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.lambda$startMonitor$2(DirectMessageListenerContainer.java:453) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54) ~[spring-context-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515) ~[na:na]
	at java.base/java.util.concurrent.FutureTask.runAndReset(FutureTask.java:305) ~[na:na]
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:305) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628) ~[na:na]
	at java.base/java.lang.Thread.run(Thread.java:830) ~[na:na]
Caused by: com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/', class-id=50, method-id=10)
	at com.rabbitmq.utility.ValueOrException.getValue(ValueOrException.java:66) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.utility.BlockingValueOrException.uninterruptibleGetValue(BlockingValueOrException.java:36) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel$BlockingRpcContinuation.getReply(AMQChannel.java:502) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.privateRpc(AMQChannel.java:293) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.exnWrappingRpc(AMQChannel.java:141) ~[amqp-client-5.7.3.jar:5.7.3]
	... 19 common frames omitted
Caused by: com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/', class-id=50, method-id=10)
	at com.rabbitmq.client.impl.ChannelN.asyncShutdown(ChannelN.java:522) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.ChannelN.processAsync(ChannelN.java:346) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.handleCompleteInboundCommand(AMQChannel.java:182) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.handleFrame(AMQChannel.java:114) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection.readFrame(AMQConnection.java:672) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection.access$300(AMQConnection.java:48) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:599) ~[amqp-client-5.7.3.jar:5.7.3]
	... 1 common frames omitted

2020-05-10 12:25:42.369 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Attempting to restart consumer SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=null identity=23f761b8]
2020-05-10 12:25:42.380 DEBUG 90030 --- [ 127.0.0.1:5672] o.s.a.r.c.CachingConnectionFactory       : Channel shutdown: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/', class-id=50, method-id=10)
2020-05-10 12:25:42.380 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Detected closed channel on exception.  Re-initializing: AMQChannel(amqp://guest@127.0.0.1:5672/,1)
2020-05-10 12:25:42.388 ERROR 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Queue not present, scheduling consumer SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=null identity=319f1fe8] for restart

java.io.IOException: null
	at com.rabbitmq.client.impl.AMQChannel.wrap(AMQChannel.java:129) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.wrap(AMQChannel.java:125) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.exnWrappingRpc(AMQChannel.java:147) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.ChannelN.queueDeclarePassive(ChannelN.java:1012) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.ChannelN.queueDeclarePassive(ChannelN.java:52) ~[amqp-client-5.7.3.jar:5.7.3]
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:na]
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:na]
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:na]
	at java.base/java.lang.reflect.Method.invoke(Method.java:567) ~[na:na]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory$CachedChannelInvocationHandler.invoke(CachingConnectionFactory.java:1190) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at com.sun.proxy.$Proxy48.queueDeclarePassive(Unknown Source) ~[na:na]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.consume(DirectMessageListenerContainer.java:705) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.doConsumeFromQueue(DirectMessageListenerContainer.java:681) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.restartConsumer(DirectMessageListenerContainer.java:523) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer.lambda$startMonitor$2(DirectMessageListenerContainer.java:453) ~[spring-rabbit-2.2.5.RELEASE.jar:2.2.5.RELEASE]
	at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54) ~[spring-context-5.2.5.RELEASE.jar:5.2.5.RELEASE]
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515) ~[na:na]
	at java.base/java.util.concurrent.FutureTask.runAndReset(FutureTask.java:305) ~[na:na]
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:305) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628) ~[na:na]
	at java.base/java.lang.Thread.run(Thread.java:830) ~[na:na]
Caused by: com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/', class-id=50, method-id=10)
	at com.rabbitmq.utility.ValueOrException.getValue(ValueOrException.java:66) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.utility.BlockingValueOrException.uninterruptibleGetValue(BlockingValueOrException.java:36) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel$BlockingRpcContinuation.getReply(AMQChannel.java:502) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.privateRpc(AMQChannel.java:293) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.exnWrappingRpc(AMQChannel.java:141) ~[amqp-client-5.7.3.jar:5.7.3]
	... 19 common frames omitted
Caused by: com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/', class-id=50, method-id=10)
	at com.rabbitmq.client.impl.ChannelN.asyncShutdown(ChannelN.java:522) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.ChannelN.processAsync(ChannelN.java:346) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.handleCompleteInboundCommand(AMQChannel.java:182) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQChannel.handleFrame(AMQChannel.java:114) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection.readFrame(AMQConnection.java:672) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection.access$300(AMQConnection.java:48) ~[amqp-client-5.7.3.jar:5.7.3]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:599) ~[amqp-client-5.7.3.jar:5.7.3]
	... 1 common frames omitted

2020-05-10 12:25:52.365 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Attempting to restart consumer SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=null identity=319f1fe8]
2020-05-10 12:25:52.375 DEBUG 90030 --- [ 127.0.0.1:5672] o.s.a.r.c.CachingConnectionFactory       : Channel shutdown: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/', class-id=50, method-id=10)
2020-05-10 12:25:52.375 DEBUG 90030 --- [nsumerMonitor-1] o.s.a.r.c.CachingConnectionFactory       : Detected closed channel on exception.  Re-initializing: AMQChannel(amqp://guest@127.0.0.1:5672/,1)
2020-05-10 12:25:52.383 ERROR 90030 --- [nsumerMonitor-1] o.s.a.r.l.DirectMessageListenerContainer : Queue not present, scheduling consumer SimpleConsumer [queue=35277c24-fb82-4096-805c-5c12fd3de915, consumerTag=null identity=62c17852] for restart
```

The log output of the RabbitMQ broker (info level), after restart and connection
recovery, is repeating the following output:

```shell
rabbitmq_1  | 2020-05-10 10:25:28.922 [info] <0.8.0> Server startup complete; 3 plugins started.
rabbitmq_1  |  * rabbitmq_management
rabbitmq_1  |  * rabbitmq_management_agent
rabbitmq_1  |  * rabbitmq_web_dispatch
rabbitmq_1  |  completed with 3 plugins.
rabbitmq_1  | 2020-05-10 10:25:32.386 [info] <0.621.0> accepting AMQP connection <0.621.0> (172.19.0.1:53358 -> 172.19.0.2:5672)
rabbitmq_1  | 2020-05-10 10:25:32.394 [info] <0.621.0> Connection <0.621.0> (172.19.0.1:53358 -> 172.19.0.2:5672) has a client-provided name: rabbitConnectionFactory#30f4b1a6:3
rabbitmq_1  | 2020-05-10 10:25:32.402 [info] <0.621.0> connection <0.621.0> (172.19.0.1:53358 -> 172.19.0.2:5672 - rabbitConnectionFactory#30f4b1a6:3): user 'guest' authenticated and granted access to vhost '/'
rabbitmq_1  | 2020-05-10 10:25:32.422 [error] <0.629.0> Channel error on connection <0.621.0> (172.19.0.1:53358 -> 172.19.0.2:5672, vhost: '/', user: 'guest'), channel 1:
rabbitmq_1  | operation queue.declare caused a channel exception not_found: no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/'
rabbitmq_1  | 2020-05-10 10:25:42.397 [error] <0.635.0> Channel error on connection <0.621.0> (172.19.0.1:53358 -> 172.19.0.2:5672, vhost: '/', user: 'guest'), channel 1:
rabbitmq_1  | operation queue.declare caused a channel exception not_found: no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/'
rabbitmq_1  | 2020-05-10 10:25:52.404 [error] <0.651.0> Channel error on connection <0.621.0> (172.19.0.1:53358 -> 172.19.0.2:5672, vhost: '/', user: 'guest'), channel 1:
rabbitmq_1  | operation queue.declare caused a channel exception not_found: no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/'
rabbitmq_1  | 2020-05-10 10:26:02.388 [error] <0.667.0> Channel error on connection <0.621.0> (172.19.0.1:53358 -> 172.19.0.2:5672, vhost: '/', user: 'guest'), channel 1:
rabbitmq_1  | operation queue.declare caused a channel exception not_found: no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/'
rabbitmq_1  | 2020-05-10 10:26:12.396 [error] <0.683.0> Channel error on connection <0.621.0> (172.19.0.1:53358 -> 172.19.0.2:5672, vhost: '/', user: 'guest'), channel 1:
rabbitmq_1  | operation queue.declare caused a channel exception not_found: no queue '35277c24-fb82-4096-805c-5c12fd3de915' in vhost '/'
```
