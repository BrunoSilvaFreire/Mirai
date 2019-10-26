package me.ddevil.mirai.command

import com.google.common.base.Strings
import me.ddevil.mirai.*
import me.ddevil.mirai.mensaging.Message
import me.ddevil.util.emptyString
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageChannel

data class CommandArguments(
    val args: List<String>
)

interface CommandSender {
    fun reply(builder: Message.() -> Unit)

}

class UserSender(
    val user: Member,
    val channel: MessageChannel
) : CommandSender {
    override fun reply(builder: Message.() -> Unit) {
        val msg = Message()
        msg.builder()
        println("Replying @ ${channel} with ${msg.content}")
        channel.sendMessage(
            msg.content
        ).submit().join()
    }

}

abstract class Command(
    val name: String,
    val description: String,
    val usage: String,
    vararg aliases: String
) : AbstractToggleable() {
    val aliases = aliases.toSet()
    override suspend fun onInitialize() {

    }

    override suspend fun onTerminate() {
    }

    abstract suspend fun execute(
        args: CommandArguments,
        sender: CommandSender,
        mirai: Mirai
    )

    fun identifiedBy(cmdAlias: String): Boolean {
        return cmdAlias == name || cmdAlias in aliases
    }
}
typealias SubCommand = suspend (args: CommandArguments, sender: CommandSender, mirai: Mirai) -> Unit

open class ScopedCommand(
    name: String,
    description: String,
    usage: String,
    vararg aliases: String
) : Command(name, description, usage, *aliases) {

    val rootScope = Scope<SubCommand>(emptyString())
    fun register(subName: String, dispatcher: SubCommand) {
        rootScope.findChild(subName).meta = dispatcher
    }

    override suspend fun execute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        val fArgs = args.args
        val search = fArgs.toMutableList()

        while (search.isNotEmpty()) {
            val found = rootScope.findNullableChild(search.joinToString(separator = "."))
            val m = found?.meta
            if (m != null) {
                val transformedIndex = fArgs.indexOf(found.name)
                println(transformedIndex)
                val transformedParameters = fArgs.subList(transformedIndex + 1, fArgs.size)
                println("New parameters are $transformedParameters")
                m(CommandArguments(transformedParameters), sender, mirai)
                return
            }
            search.removeAt(search.lastIndex)
        }
        sender.reply {
            raw("Sub Commands:")
            fun addScope(scope: Scope<SubCommand>, indent: Int) {
                for (child in scope.children) {
                    raw("${"-".repeat(indent)}${child.name}")
                    addScope(child, indent + 1)
                }
            }
            addScope(rootScope, 0)
        }
    }

}