import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream

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

    registerTransform {
        from.attribute(graalRuntimeAttr, false)
        to.attribute(graalRuntimeAttr, true)
        artifactTransform(ReduceGroovyRuntime::class)
    }
    artifactTypes.getByName("jar") {
        attributes.attribute(graalRuntimeAttr, false)
    }
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
}

class ReduceGroovyRuntime : ArtifactTransform() {
    val exclude = setOf(
            "META-INF/INDEX.LIST",
            "groovyjarjar",
            "groovy/grape",
            "groovy/transform",
            "groovy/ui",
            "groovy/xml",
            "groovy/lang/Closure",
            "groovy/lang/GString$",
            "groovy/lang/MetaClassImpl",
            "groovy/lang/GroovySystem",
            "org/codehaus/groovy/classgen/asm/indy",
            "org/codehaus/groovy/transform",
            "org/codehaus/groovy/plugin",
            "org/codehaus/groovy/syntax",
            "org/codehaus/groovy/antlr",
            "org/codehaus/groovy/reflection/ClassInfo",
            "org/codehaus/groovy/reflection/Cached",
            "org/codehaus/groovy/control"
    )

    override fun transform(inputJar: File): MutableList<File> = inputJar.inputStream().use {
        val jarInputStream = JarInputStream(it)
        val outFile = File(outputDirectory, inputJar.name)
        println("Generating minimal Groovy Graal runtime to $outFile")
        JarOutputStream(outFile.outputStream()).use { out ->
            var entry = jarInputStream.nextJarEntry
            while (entry != null) {
                val includeEntry = !exclude.any { entry.name.startsWith(it) }
                if (includeEntry) {
                    out.putNextEntry(entry)
                    if (!entry.isDirectory) {
                        jarInputStream.copyTo(out)
                    }
                    out.closeEntry()
                }
                jarInputStream.closeEntry()
                entry = jarInputStream.nextJarEntry
            }
            jarInputStream.close()
        }
        mutableListOf(outFile)
    }

}