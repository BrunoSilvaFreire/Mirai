package me.ddevil.mirai.command

import me.ddevil.mirai.Mirai
import me.ddevil.mirai.mensaging.Message

typealias DebugProvider = Message.() -> Unit

class DebugCommand(mirai: Mirai) : Command("debug", "Shows bot info", "debug", mirai) {
    data class RegisteredProvider(
        val id: Prefixed,
        val provider: DebugProvider

    )

    private val providers = ArrayList<RegisteredProvider>()

    fun register(owner: Prefixed, provider: DebugProvider) {
        providers += RegisteredProvider(owner, provider)
    }

    fun deregister(owner: Prefixed) {
        providers.removeAll { it.id == owner }
    }

    override suspend fun onExecute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        val user = sender as? MemberSender
        if (user == null) {
            sender.reply {
                markError()
                raw("This command is only executable as user")
            }
            return
        }

        user.member.user.openPrivateChannel().queue {
            sender.reply {
                raw("Debug info sent to your private channel.")
            }
            TextSender(it).reply {
                for (provider in providers) {
                    markdown(provider.id.prefix, bold = true)
                    provider.provider(this)
                }
            }
        }
    }

}