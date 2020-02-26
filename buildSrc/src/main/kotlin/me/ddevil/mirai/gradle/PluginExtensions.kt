package me.ddevil.mirai.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File


fun Project.addPluginToBotsTasks() {
    val botTasks: List<JavaExec>? by rootProject.extra
    val toAddTo: List<JavaExec>
    if (botTasks == null) {
        logger.error("Unable to find botTasks property on bot project.")
        return
    } else {
        toAddTo = botTasks!!
    }
    for (bot in toAddTo) {
        val workDir = File(bot.workingDir, "plugins")
        val pluginTask = tasks.create<Copy>("copyPluginIntoBot-${bot.name}") {
            group = "mirai"
            from(project.tasks.named("jar"))
            into(workDir)
        }
        bot.dependsOn(pluginTask)
    }
}