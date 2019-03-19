## A simple HTTP server using GraalVM

This project demonstrates the creation of a simple HTTP web server using the embedded JDK server.
It generates a native image using [Graal](https://github.com/oracle/graal).

There are two subprojects:

* one written in Kotlin
* one written in Groovy

in both cases, running `./gradlew nativeImage` will generate the server.

The resulting image is ~11MB in both languages.

See https://melix.github.io/blog/2019/03/simple-http-server-graal.html for context.
