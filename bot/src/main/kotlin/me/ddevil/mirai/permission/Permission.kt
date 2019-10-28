package me.ddevil.mirai.permission

import me.ddevil.mirai.Mirai
import me.ddevil.mirai.Scope
import me.ddevil.mirai.persistence.*
import me.ddevil.util.getString
import net.dv8tion.jda.api.entities.Role

class Permission(
    val name: String
)

enum class Grant {
    ALLOW,
    DENY,
    IGNORE
}