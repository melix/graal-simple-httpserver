plugins {
   kotlin("jvm") version "1.3.21"
   id("com.palantir.graal") version "0.3.0-6-g0b828af"
}

repositories {
   jcenter()
}

dependencies {
   implementation(kotlin("stdlib"))
}

graal {
   graalVersion("1.0.0-rc14")
   mainClass("HttpServerKt")
   outputName("httpserv-kt")
   option("--enable-http")
}