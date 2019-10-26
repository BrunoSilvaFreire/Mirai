plugins {
    kotlin("jvm") version "1.3.50"
    application
}
dependencies {
    api(kotlin("stdlib-jdk8"))
    api("me.ddevil:json:1.2.0-SNAPSHOT")
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