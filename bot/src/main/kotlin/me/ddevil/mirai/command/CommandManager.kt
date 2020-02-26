package me.ddevil.mirai.command

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.ddevil.mirai.Mirai
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory
import java.lang.Exception

class CommandManager(
    val mirai: Mirai
) : EventListener, Prefixed {

    val logger = LoggerFactory.getLogger(CommandArguments::class.java)
    override fun onEvent(event: GenericEvent) {
        if (event is MessageReceivedEvent) {
            val m = event.member ?: return
            val ch = event.channel
            val msg = event.message
            if (!msg.contentDisplay.startsWith(mirai.commandPrefix)) {
                return
            }
            val content = msg.contentDisplay.removePrefix(mirai.commandPrefix).split(' ')
            if (content.isEmpty()) {
                return
            }
            val cmdAlias = content.first()
            val cmd = findCommand(cmdAlias) ?: return
            val args = if (content.size == 1) {
                emptyArray()
            } else {
                content.slice(1 until content.size).toTypedArray()
            }
            GlobalScope.launch {
                val sender = MemberSender(m, ch)
                try {
                    cmd.execute(
                        CommandArguments(args),
                        sender,
                        mirai
                    )
                } catch (e: Exception) {
                    sender.reply {
                        markError()
                        title = e::class.java.simpleName

                        e.message?.let {
                            raw(it)
                        }
                    }
                    throw e
                }
            }
        }
    }

    private fun findCommand(cmdAlias: String): Command? {
        return commands.firstOrNull { it.identifiedBy(cmdAlias) }
    }


    val commands = ArrayList<Command>()

    init {
        logger.info("Hooking command manager")

        mirai.jda.addEventListener(this)
        runBlocking {
            logger.info("Registering built in commands")
            register(VersionCommand(mirai))
            register(ListCommandsCommand(this@CommandManager))
            register(BotCommand(mirai))
            register(PluginCommand(mirai.pluginManager))
            register(TodoCommand(mirai))
            register(PermissionCommand(mirai))
            register(DebugCommand(mirai))
        }
        withCommandOf<DebugCommand> {
            register(this@CommandManager) {
                raw("Total of ${commands.size} commands loaded.")
                for (cmd in commands) {
                    markdown(cmd.name, italic = true)
                    raw("Permission: ${cmd.permission}")
                    raw("Owner: ${cmd.owner.prefix} *(${cmd.owner::class.java.name})*")
                    separator()
                }
            }
        }
    }

    suspend fun register(command: Command) {
        commands += command
        command.initialize()
    }

    suspend fun deregister(command: Command) {
        commands -= command
        command.terminate()
    }

    inline fun <reified T : Command> withCommandOf(function: T.() -> Unit) {
        for (command in commands) {
            if (command is T) {
                command.function()
            }
        }
    }

    override val prefix: String
        get() = "mirai.command_manager"
}
