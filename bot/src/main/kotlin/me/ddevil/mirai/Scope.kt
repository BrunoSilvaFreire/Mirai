package me.ddevil.mirai

import me.ddevil.util.emptyString


data class Scope<M>(
    val name: String,
    var meta: M? = null,
    val children: MutableList<Scope<M>> = ArrayList()
) {
    companion object {
        fun getParts(scope: String) = scope.split('.')
        fun <M> root(): Scope<M> = Scope(emptyString())
    }

    fun findDirectChild(name: String) = children.firstOrNull {
        it.name == name
    }

    fun findChild(scope: String): Scope<M> {
        val parts = getParts(scope).toMutableList()
        var current = this
        while (parts.isNotEmpty()) {
            val name = parts.first()
            var candidate = current.findDirectChild(name)
            if (candidate == null) {
                candidate = Scope(name)
                current.children += candidate
            }
            current = candidate
            parts -= name
        }
        return current
    }

    fun findNullableChild(scope: String): Scope<M>? {
        return findNullableChild(getParts(scope).toMutableList())

    }

    fun findNullableChild(parts: MutableList<String>): Scope<M>? {
        var current = this
        while (parts.isNotEmpty()) {
            val name = parts.first()
            current = current.findDirectChild(name) ?: return null
            parts -= name
        }
        return current
    }
}
