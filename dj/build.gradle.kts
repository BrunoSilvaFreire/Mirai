import me.ddevil.mirai.gradle.addPluginToBotsTasks

plugins {
    kotlin("jvm")
}
dependencies {
    api(project(":bot"))
}

repositories {
    jcenter()
    mavenLocal()
}
addPluginToBotsTasks()