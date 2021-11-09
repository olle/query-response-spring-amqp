Query/Response Examples
=======================

In order to show how the Query/Response API works, and provide developers with
a setup to get some hands-on experience, we've provided a small example setup.

Running the examples
--------------------

1. Start the RabbitMQ server container by running `make` in this directory.
   Alternatively `make up` for detached-mode, and later `make down` to stop
   the broker.

2. Open both the `querying/` and `responding/` directories in a terminal window,
   next to each other.

3. Each of the example _sides_ can be started by simply running `make`. And will
   stop after pressing `CTRL-C`.
