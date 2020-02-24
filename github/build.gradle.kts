plugins {
    kotlin("jvm")
}
dependencies {
    api(project(":bot"))
    api("org.kohsuke:github-api:1.101")
}

repositories {
    jcenter()
    mavenLocal()
}