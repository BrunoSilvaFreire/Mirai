package me.ddevil.mirai.persistence

import me.ddevil.mirai.hashMap
import me.ddevil.util.Serialized


interface Persistence {
    suspend fun all(): List<Serialized>
    suspend fun <K> get(key: K): Serialized?
    suspend fun <K> set(key: K, obj: Serialized)
}

suspend fun <K> Persistence.set(key: K, builder: HashMap<String, Any>.() -> Unit) {
    set(key, hashMap(builder))
}