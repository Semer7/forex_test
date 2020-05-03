Sample project to emulate forex data provider

Throughoutput: basic local2local test with Jmeter showed 1M requests per minute.

Request format:
`http://<host>:<port>/rates?from=<currency>&to=<currency>`

Supported currencies:
 - USD
 - JPY
 - AUD
 - CAD
 - CHF
 - EUR
 - GBP
 - NZD
 - SGD

Request example:
`curl --location --request GET 'localhost:8585/rates?from=USD&to=JPY'`

Data source is following docker image:
`docker pull paidyinc/one-frame`

run it using `docker run -p <port>:<port> paidyinc/one-frame`
default `docker run -p 8080:8080 paidyinc/one-frame`

Requirements Scala 2.13, sbt 1.3.10

To run application use `sbt run` in root folder
To package as JAR use `sbt package` and .jar will appear in /target/scala-2.13

App configuration:
Resides in resource folder. To be updated.

TODO:
 - graceful shutdown
 - multiple currencies per request support
 - rewrite data folding from one-frame
 - add guice
 - add configurable path to config file
 - read config properties from environmental variables
 - wrap artifact in docker image
 - cover with tests
 - commence proper load test