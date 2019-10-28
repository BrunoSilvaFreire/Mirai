package me.ddevil.mirai.command

import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments
import me.ddevil.mirai.Mirai
import me.ddevil.mirai.permission.Grant
import me.ddevil.util.exception.ArgumentOutOfRangeException
import java.awt.Color

class PermissionCommand(val mirai: Mirai) :
    ScopedCommand(
        "permissions",
        "Muda permissões de roles",
        "/perms (set, remove, list)",
        "perms"
    ) {
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

                }
            } catch (e: ArgumentOutOfRangeException) {

            }
        }
    }


}