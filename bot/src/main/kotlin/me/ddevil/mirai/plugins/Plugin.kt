package me.ddevil.mirai.plugins

import me.ddevil.json.parse.JsonParser
import me.ddevil.mirai.Mirai
import java.io.InputStream
import java.util.logging.Logger

data class PluginDescriptor(
    val group: String,
    val name: String,
    val version: String,
    val main: String
) {
    val identifier get() = "${group}.${name}"

    companion object {
        fun from(stream: InputStream): PluginDescriptor {
            val json = JsonParser().parseObject(stream)
            return PluginDescriptor(
                json.getString("group"),
                json.getString("name"),
                json.getString("version"),
                json.getString("main")
            )
        }
    }
}

abstract class Plugin {
    lateinit var pluginDescriptor: PluginDescriptor
        private set
    lateinit var logger: Logger
    lateinit var mirai: Mirai
        private set

    fun initialize(
        pluginDescriptor: PluginDescriptor,
        mirai: Mirai
    ) {
        this.pluginDescriptor = pluginDescriptor
        logger = Logger.getLogger(pluginDescriptor.identifier)
        this.mirai = mirai
        bootstrap()
    }

    open fun bootstrap() {}
}