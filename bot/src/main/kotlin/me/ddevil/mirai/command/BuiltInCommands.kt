package me.ddevil.mirai.command

import me.ddevil.mirai.Mirai
import me.ddevil.mirai.kMiraiVersion

class VersionCommand : Command(
    "version",
    "Mostra a versão da Mirai",
    "/version"
) {
    override suspend fun execute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        sender.reply {
            raw(kMiraiVersion)
        }
    }
}

class ListCommandsCommand : Command(
    "commands",
    "Lista todos os comandos disponíveis",
    "/commands"
) {
    override suspend fun execute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        sender.reply {
            for (command in mirai.commandManager.commands) {
                raw("**${command.name}**: ${command.description}")
                raw("Uso: ${command.usage}")
            }
        }
    }
}

class BotCommand : Command(
    "bot",
    "Envia uma mensagem de informação",
    "/bot",
    "mirai"
) {
    override suspend fun execute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
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