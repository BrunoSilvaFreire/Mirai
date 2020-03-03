import me.ddevil.mirai.gradle.addPluginToBotsTasks
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val penicillinVersion = "4.2.3"
val ktorVersion = "1.3.0-rc"

plugins {
    kotlin("jvm")
}
dependencies {
    api(project(":bot"))
    implementation("jp.nephy:penicillin:$penicillinVersion")
}

repositories {
    maven(url = "https://dl.bintray.com/nephyproject/stable")
}
addPluginToBotsTasks()
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}