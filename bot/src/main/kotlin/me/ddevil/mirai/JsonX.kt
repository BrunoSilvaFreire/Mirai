package me.ddevil.mirai

import me.ddevil.json.JsonObject

fun JsonObject.getJsonOrInsert(key: String): JsonObject {
    var child = getJsonOrNull(key)
    if (child == null) {
        child = JsonObject()
        this[key] = child
    }
    return child
}