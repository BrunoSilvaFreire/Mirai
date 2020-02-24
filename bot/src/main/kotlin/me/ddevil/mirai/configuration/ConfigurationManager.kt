package me.ddevil.mirai.configuration

import me.ddevil.json.JsonObject
import me.ddevil.json.parse.JsonParser
import me.ddevil.mirai.getJsonOrInsert
import me.ddevil.mirai.persistence.DataScope
import me.ddevil.mirai.plugins.Plugin
import me.ddevil.util.Serialized
import java.io.File

class JsonDataScope(
    val json: JsonObject
) : DataScope {
    override suspend fun all(): List<Serialized> = json.keys.filterIsInstance<Serialized>()

    override suspend fun <K> get(key: K): Serialized? {
        return json.getJsonOrNull(key.toString())
    }

    override suspend fun <K> set(key: K, obj: Serialized) {
        json[key.toString()] = obj
    }

    override suspend fun <K> delete(key: K): Boolean {
        return json.remove(key.toString()) != null
    }

}

class ConfigurationManager(
    val file: File
) {
    private val config: JsonObject

    init {
        if (!file.exists()) {
            config = JsonObject()
            file.writeText(config.toJson())
        } else {
            config = JsonParser().parseObject(file)
        }
    }

    val miraiScope = JsonDataScope(config.getJsonOrInsert("mirai"))


    fun pluginScope(plugin: Plugin): JsonDataScope {
        val pluginsJson = config.getJsonOrInsert("plugins")
        return JsonDataScope(pluginsJson.getJsonOrInsert(plugin.pluginDescriptor.identifier))
    }
}