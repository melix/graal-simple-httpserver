
plugins {
    groovy
    `java-library`
    id("com.palantir.graal") version "0.3.0-6-g0b828af"
}

repositories {
    jcenter()
}

val graalRuntimeAttr = Attribute.of("graalRuntime", Boolean::class.javaObjectType)

dependencies {
    implementation("org.codehaus.groovy:groovy:2.5.6")
}

configurations {
    runtimeClasspath {
        attributes {
            attribute(graalRuntimeAttr, true)
        }
    }
}

tasks {
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    compileGroovy {
        targetCompatibility = "1.6"
    }
}

graal {
    graalVersion("1.0.0-rc14")
    mainClass("HttpServerGroovy")
    outputName("httpserv-groovy")
    option("--enable-http")
    option("--report-unsupported-elements-at-runtime")
    option("--allow-incomplete-classpath")
    option("-H:+ReportExceptionStackTraces")
    option("-H:ConfigurationFileDirectories=graal/")
    option("--delay-class-initialization-to-runtime=org.codehaus.groovy.control.XStreamUtils,groovy.grape.GrapeIvy")
}
