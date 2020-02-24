package me.ddevil.mirai.permission

import me.ddevil.mirai.Mirai
import me.ddevil.mirai.Scope
import me.ddevil.mirai.persistence.DataScope
import me.ddevil.mirai.persistence.loadScope
import me.ddevil.mirai.persistence.setScope
import net.dv8tion.jda.api.entities.Role


class PermissionGroup
private constructor(
    val root: Scope<Grant>,
    val role: Role,
    val persistence: DataScope
) {

    companion object {
        suspend fun getFor(role: Role, mirai: Mirai): PermissionGroup {
            val persistence = mirai.persistenceManager.request("permissions")
            val found = persistence.get(role.name)
            val root = if (found == null) {
                Scope.root()
            } else {
                loadScope(found) {
                    Grant.valueOf(it as String)
                }
            }
            return PermissionGroup(
                root, role, persistence
            )
        }
    }


    suspend fun grant(permission: String, grant: Grant) {
        root.findChild(permission).meta = grant
        persistence.setScope(role.id, root)
    }

    fun test(permission: String): Boolean {
        val open = Scope.getParts(permission).toMutableList()
        while (open.isNotEmpty()) {
            val found = root.findNullableChild(open.toMutableList())
            val g = found?.meta
            if (found == null || g == null || g == Grant.IGNORE) {
                open.removeAt(open.lastIndex)
            } else {
                return g == Grant.ALLOW
            }
        }
        return false
    }

}