plugins {
    id("io.wusa.semver-git-plugin") version "2.0.0-alpha.1"
}
val ver = semver.info
allprojects {
    group = "me.ddevil.mirai"
    version = ver
}