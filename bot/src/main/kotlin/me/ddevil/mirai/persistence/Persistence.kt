package me.ddevil.mirai.persistence

import me.ddevil.mirai.Scope
import me.ddevil.mirai.hashMap
import me.ddevil.mirai.permission.Grant
import me.ddevil.util.*


interface Persistence {
    suspend fun all(): List<Serialized>
    suspend fun <K> get(key: K): Serialized?
    suspend fun <K> set(key: K, obj: Serialized)
    suspend fun <K> delete(key: K): Boolean
}

suspend fun <K> Persistence.set(key: K, builder: HashMap<String, Any?>.() -> Unit) {
    set(key, hashMap(builder))
}

suspend fun <K, M> Persistence.setScope(key: K, scope: Scope<M>, transform: ((M) -> Any)? = null) {
    fun transform(scope: Scope<M>): Serialized {
        val hashMap = HashMap<String, Any?>()
        val meta = scope.meta
        hashMap["name"] = scope.name
        if (meta != null) {
            hashMap["meta"] = transform?.invoke(meta) ?: meta
        }
        val children = scope.children

        if (children.isNotEmpty()) {
            hashMap["children"] = children.map { transform(it) }
        }
        return hashMap
    }
    set(key) {
        putAll(transform(scope))
    }
}

suspend fun <K, M> Persistence.setSerializableScope(key: K, scope: Scope<M>) where M : Serializable =
    setScope(key, scope) {
        return@setScope it.serialize()
    }

fun <M> loadScope(serialized: Serialized, loader: (Any) -> M): Scope<M> {

    val name = serialized.getString("name")
    val children = serialized.getListOrNull<Serialized>("children")
    val scope = Scope<M>(name)
    val meta = serialized["meta"]
    if (children != null && children.isNotEmpty()) {
        scope.children.addAll(
            children.map {
                loadScope(it, loader)
            }
        )
    }
    if (meta != null) {
        scope.meta = loader(meta)
    }
    return scope

}