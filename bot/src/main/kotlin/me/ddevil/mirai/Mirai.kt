package me.ddevil.mirai

import me.ddevil.mirai.command.CommandManager
import me.ddevil.mirai.persistence.FilePersistenceFactory
import me.ddevil.mirai.persistence.PersistenceManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

const val kMiraiVersion = "v0.0.1-SNAPSHOT"

class Mirai(
    token: String
) {
    fun run() {
        jda.awaitReady()
    }

    val jda: JDA
    val logger: Logger
    val persistenceManager: PersistenceManager
    val commandManager: CommandManager

    init {
        logger = LoggerFactory.getLogger(Mirai::class.java)
        logger.info("Connecting with token '$token'")
        persistenceManager = PersistenceManager(
            factory = FilePersistenceFactory(File("storage"))
        )
        jda = JDABuilder()
            .setToken(token)
            .build()
        commandManager = CommandManager(this)
    }
}