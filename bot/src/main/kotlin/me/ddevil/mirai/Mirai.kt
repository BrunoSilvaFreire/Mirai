package me.ddevil.mirai

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import me.ddevil.mirai.command.CommandManager
import me.ddevil.mirai.command.CommandOwner
import me.ddevil.mirai.configuration.ConfigurationManager
import me.ddevil.mirai.permission.PermissionManager
import me.ddevil.mirai.persistence.FilePersistenceFactory
import me.ddevil.mirai.persistence.PersistenceManager
import me.ddevil.mirai.plugins.PluginManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


class Mirai : CliktCommand(), CommandOwner {
    val token by option(help = "The token to use for the bot")
    val commandPrefix by option(help = "Prefix used for commands").default("!")
    lateinit var configurationManager: ConfigurationManager
    override fun run() {
        logger.info("Connecting with token '$token'")
        jda = JDABuilder()
            .setToken(token)
            .build()
        jda.awaitReady()
        persistenceManager = PersistenceManager(
            factory = FilePersistenceFactory(File("storage"))
        )
        permissionManager = PermissionManager(this)

        commandManager = CommandManager(this)
        val configFile = File("config.json")
        logger.info("Using config file '${configFile.absolutePath}'")
        configurationManager = ConfigurationManager(configFile)
        pluginManager = PluginManager(this, File("plugins"))
    }

    val logger = LoggerFactory.getLogger(Mirai::class.java)
    lateinit var jda: JDA
    lateinit var persistenceManager: PersistenceManager
    lateinit var commandManager: CommandManager
    lateinit var permissionManager: PermissionManager
    lateinit var pluginManager: PluginManager
    override val prefix: String
        get() = "mirai"

}