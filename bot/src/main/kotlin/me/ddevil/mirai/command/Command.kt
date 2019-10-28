package me.ddevil.mirai.command

import com.google.common.base.Strings
import me.ddevil.mirai.*
import me.ddevil.mirai.mensaging.Message
import me.ddevil.util.emptyString
import me.ddevil.util.exception.ArgumentOutOfRangeException
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.internal.entities.EntityBuilder

class CommandArguments
constructor(
    private val args: Array<String>
) {
    val arguments: Array<String>
        get() = arrayOf(*args)
    val length get() = args.size
    val fullText get() = args.joinToString(" ")

    @Throws(ArgumentOutOfRangeException::class)
    fun getString(index: Int): String {
        checkOutOfRange(index)
        return arguments[index]
    }

    private fun checkOutOfRange(index: Int) {
        if (isOutOfRange(index)) {
            throw ArgumentOutOfRangeException("index", index)
        }
    }

    @Throws(ArgumentOutOfRangeException::class, NumberFormatException::class)
    fun getFloat(index: Int): Float {
        return getString(index).toFloat()
    }

    @Throws(ArgumentOutOfRangeException::class, NumberFormatException::class)
    fun getInt(index: Int): Int {
        return getString(index).toInt()
    }


    @Throws(ArgumentOutOfRangeException::class)
    fun getBoolean(index: Int): Boolean {
        return getString(index).toBoolean()
    }

    fun isOutOfRange(index: Int) = index < 0 || index >= length
    fun getDouble(i: Int): Double {
        return getString(i).toDouble()
    }
}

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
        if (msg.isRich()) {
            channel.sendMessage(
                EmbedBuilder()
                    .setTitle(msg.title)
                    .setColor(msg.color)
                    .setDescription(msg.content)
                    .build()

            ).submit().join()
        } else {
            channel.sendMessage(
                msg.content
            ).submit().join()
        }

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
        val fArgs = args.arguments
        val search = fArgs.toMutableList()

        while (search.isNotEmpty()) {
            val found = rootScope.findNullableChild(search.joinToString(separator = "."))
            val m = found?.meta
            if (m != null) {
                val transformedIndex = fArgs.indexOf(found.name)
                val transformedParameters = fArgs.sliceArray(transformedIndex + 1 until fArgs.size)
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