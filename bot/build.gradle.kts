import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.ddevil.mirai.gradle.BotConfig
import java.util.Properties

plugins {
    kotlin("jvm")
    application
}
buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.squareup:kotlinpoet:1.4.1")
    }
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
    maven {
        name = "lunari"
        url = uri("https://repo.lunari.studio/repository/maven-public/")
    }
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


val botsFile = rootProject.file("bots.properties")
if (botsFile.exists()) {
    val prop = Properties()
    prop.load(botsFile.inputStream())
    val bots = ArrayList<BotConfig>()
    loop@ for ((key, value) in prop) {
        if (key !is String) {
            continue
        }

        if (key.contains('.')) {
            //Is bot property
            val (botName, property) = key.split(".")
            val bot = bots.firstOrNull { it.botId == botName }
            if (bot == null) {
                logger.warn("Unable to find bot with name $botName.")
                continue
            }
            when (property) {
                "commandPrefix" -> {
                    if (value !is String) {
                        logger.warn("commandPrefix must be a string")
                        continue@loop
                    }
                    bot.commandPrefix = value.first()
                }
            }
            continue
        }
        //Is new bot
        if (value !is String) {
            logger.warn("Expected bot config $key to be a discord bot key as a string")
            continue
        }
        bots += BotConfig(key, value)
    }
    val botTasks = ArrayList<JavaExec>()
    val classPath = sourceSets.main.get().runtimeClasspath
    for (bot in bots) {
        val botDir = File(rootProject.buildDir, "runtime/${bot.botId}")
        val cleanTask = rootProject.tasks.create<Delete>("cleanBot-${bot.botId}") {
            group = "mirai"
            delete(File(botDir, "plugins"))
        }

        botTasks += rootProject.tasks.create<JavaExec>("runBot-${bot.botId}") {
            group = "mirai"
            main = "me.ddevil.mirai.MainKt"
            dependsOn(cleanTask)
            workingDir(botDir)
            classpath(classPath)
            args(
                "--command-prefix=${bot.commandPrefix ?: '?'}",
                "--token=${bot.key}"
            )

        }
    }
    rootProject.extra.set("botTasks", botTasks)

}