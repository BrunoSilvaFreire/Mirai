package me.ddevil.mirai.plugins

import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class PluginDescriptor(
    val group: String,
    val name: String,
    val version: String,
    val main: String
) {
    val identifier get() = "${group}.${name}"
}

abstract class Plugin {
    lateinit var pluginDescriptor: PluginDescriptor
        private set
    lateinit var logger: Logger
    fun initialize(
        pluginDescriptor: PluginDescriptor
    ) {
        this.pluginDescriptor = pluginDescriptor
        logger = LoggerFactory.getLogger(pluginDescriptor.identifier)

    }
}