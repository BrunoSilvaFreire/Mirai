import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.jetbrains.kotlin.codegen.ClassBuilderOnDemand

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.squareup:kotlinpoet:1.4.1")
    }
}
plugins {
    kotlin("jvm") version "1.3.50"
    application
}
dependencies {
    api("com.github.ajalt:clikt:2.2.0")
    api(kotlin("stdlib-jdk8"))
    api("me.ddevil:json:1.2.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    api("net.dv8tion:JDA:4.0.0_54") {
        exclude(module = "opus-java")
    }
}
application {
    mainClassName = "me.ddevil.mirai.MainKt"
}

repositories {
    jcenter()
    mavenLocal()
}
sourceSets {
    main {
        java {
            srcDir("src/main/generated")
        }
    }
}
val generateSources by tasks.creating {
    doFirst {
        with(FileSpec.builder("me.ddevil.mirai", "MiraiConstants")) {
            addType(TypeSpec.Companion.objectBuilder("MiraiConstants").apply {
                addProperty(
                    PropertySpec.builder(
                        "version",
                        String::class,
                        KModifier.CONST
                    ).initializer("%S", version.toString())
                        .build()
                )
            }.build()).build()
        }.writeTo(
            sourceSets.main.get().java.srcDirs.first { it.name == "generated" }
        )
    }
}
tasks.compileKotlin {
    dependsOn(generateSources)
}