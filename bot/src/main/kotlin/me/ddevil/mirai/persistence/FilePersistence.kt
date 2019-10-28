package me.ddevil.mirai.persistence

import me.ddevil.json.JsonObject
import me.ddevil.json.parse.JsonParser
import me.ddevil.mirai.Scope
import me.ddevil.util.Serialized
import java.io.File

class FilePersistenceFactory(
    val root: File
) : PersistenceFactory {
    override fun create(scope: Scope<Persistence>, parts: List<String>): Persistence {
        val directory = File(root, parts.joinToString(separator = "/"))
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return FilePersistence(directory)
    }

}

class FilePersistence(
    val directory: File
) : Persistence {
    val parser = JsonParser()
    private fun load(file: File) = parser.parseObject(file)

    private fun save(file: File, obj: Serialized) {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(JsonObject(obj).toJson())
    }

    override suspend fun all(): List<Serialized> {
        return directory.listFiles()!!.map(this::load)
    }

    override suspend fun <K> get(key: K): Serialized? {
        val file = File(directory, "$key.json")
        if (!file.exists()) {
            return null
        }
        return load(file)
    }

    override suspend fun <K> set(key: K, obj: Serialized) {
        save(File(directory, "$key.json"), obj)
    }


}