package me.ddevil.mirai.command

import me.ddevil.mirai.Mirai
import me.ddevil.mirai.MiraiConstants
import me.ddevil.mirai.plugins.PluginManager

class VersionCommand(mirai: Mirai) : Command(
    "version",
    "Mostra a versão da Mirai",
    "/version",
    mirai
) {
    override suspend fun onExecute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        sender.reply {
            raw(MiraiConstants.version)
        }
    }
}

class ListCommandsCommand(commandManager: CommandManager) : Command(
    "commands",
    "Lista todos os comandos disponíveis",
    "/commands",
    commandManager
) {
    override suspend fun onExecute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        sender.reply {
            for (command in mirai.commandManager.commands) {
                raw("**${command.name}**: ${command.description}")
                raw("Uso: ${command.usage}")
            }
        }
    }
}

class BotCommand(
    mirai: Mirai
) : Command(
    "bot",
    "Envia uma mensagem de informação",
    "/bot",
    mirai,
    "mirai"

) {
    override suspend fun onExecute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        sender.reply {
            raw("Eu sou a Mirai!")
            raw("Eu que gerêncio o role pra não virar zorra")
            raw(
                "Caso queira adicionar alguma funcionalidade, contribua em: " +
                        "https://github.com/BrunoSilvaFreire/Mirai"
            )
        }
    }
}

class PluginCommand(
    pluginManager: PluginManager
) : Command(
    "plugins",
    "Lista os plugins carregados",
    "/plugins",
    pluginManager
) {
    override suspend fun onExecute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        sender.reply {
            val pl = mirai.pluginManager.plugins
            raw("Existem ${pl.size} plugins carregados.")
            val verbose by args
            for ((i, plugin) in pl.withIndex()) {
                with(plugin.pluginDescriptor) {
                    markdown("#$i: ${this.name} v${this.version}", bold = true)
                    markdown("(${this.identifier})", italic = true)
                    if (verbose && plugin is CommandOwner) {
                        for (command in mirai.commandManager.commands) {
                            if (command.owner != plugin) {
                                continue
                            }
                            raw("Owns command ${command.name}.")
                        }
                    }
                }
            }
        }
    }

}