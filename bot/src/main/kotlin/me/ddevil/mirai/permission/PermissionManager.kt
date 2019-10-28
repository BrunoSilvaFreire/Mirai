package me.ddevil.mirai.permission

import me.ddevil.mirai.Mirai
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

class PermissionManager(
    val mirai: Mirai
) {
    private val groups = ArrayList<PermissionGroup>()
    suspend fun getGroup(role: Role): PermissionGroup {
        val found = groups.firstOrNull {
            it.role == role
        }
        if (found != null) {
            return found
        }
        return with(groups) {
            val loaded = PermissionGroup.getFor(role, mirai)
            add(loaded)
            return@with loaded
        }
    }


    suspend fun getGroupById(role: Long): PermissionGroup? {
        return getGroup(mirai.jda.getRoleById(role) ?: return null)
    }

    suspend fun getGroupById(role: String): PermissionGroup? {
        return getGroup(mirai.jda.getRoleById(role) ?: return null)
    }
}