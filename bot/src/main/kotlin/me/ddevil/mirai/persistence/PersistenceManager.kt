package me.ddevil.mirai.persistence

import me.ddevil.mirai.Scope
import me.ddevil.util.emptyString

interface PersistenceFactory {
    fun create(scope: Scope<DataScope>, parts: List<String>): DataScope
}


class PersistenceManager(
    val factory: PersistenceFactory
) {
    val rootScope = Scope<DataScope>(emptyString())


    fun request(scope: String): DataScope {
        val found = rootScope.findChild(scope)
        var m = found.meta
        if (m == null) {
            m = factory.create(found, Scope.getParts(scope))
            found.meta = m
        }
        return m
    }
}
