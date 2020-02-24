package me.ddevil.mirai.command

import me.ddevil.mirai.Mirai
import me.ddevil.mirai.mensaging.Message
import me.ddevil.mirai.permission.Grant
import me.ddevil.util.exception.ArgumentOutOfRangeException
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Color

class PermissionCommand(val mirai: Mirai) :
    ScopedCommand(
        "permissions",
        "Muda permissões de roles",
        "/perms (set, remove, list)",
        mirai,
        "perms"

    ) {
    //O cara de bigode é mt chato


    private fun Message.addRoles(sender: CommandSender) {
        this.title = "Roles & Códigos"
        var roles = mirai.jda.roles
        if (sender is UserSender) {
            val ch = sender.channel
            if (ch is TextChannel) {
                roles = roles.filter { it.guild == ch.guild }
            }
        }
        for (role in roles) {
            markdown("${role.name}:", bold = true)
            raw(role.id)

        }
    }

    init {

        register("set") { args, sender: CommandSender, mirai: Mirai ->
            try {
                val role = args.getString(0)
                try {
                    val perm = args.getString(1)
                    val group = mirai.permissionManager.getGroupById(role)
                    if (group == null) {
                        sender.reply {
                            markError()
                            title = "Grupo inexistente"
                            raw("Id $perm inválida")
                        }
                        return@register
                    }


                    val grant = try {
                        val candidate = args.getString(2)
                        Grant.valueOf(candidate)
                    } catch (e: ArgumentOutOfRangeException) {
                        Grant.ALLOW
                    } catch (e: IllegalArgumentException) {
                        Grant.ALLOW
                    }
                    group.grant(perm, grant)
                    sender.reply {
                        title = "Sucesso!"
                        color = Color.BLUE
                        raw("Permissão $perm setada para $grant no grupo ${group.role.name}")
                    }
                } catch (e: ArgumentOutOfRangeException) {
                    sender.reply {
                        markError()
                        title = "Permissão faltante"
                        markdown("Você deve informar uma permissão! Ex: !perms set (id) $permission")
                    }
                }
            } catch (e: ArgumentOutOfRangeException) {
                sender.reply {
                    markError()
                    title = "Role faltante"
                    markdown("Você deve informar o código de um Role!", bold = true)
                    addRoles(sender)
                }
            }
        }
    }


}