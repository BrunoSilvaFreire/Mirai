package me.ddevil.mirai.command

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ddevil.mirai.Mirai
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory

class CommandManager(
    val mirai: Mirai
) : EventListener {
    val logger = LoggerFactory.getLogger(CommandArguments::class.java)
    override fun onEvent(event: GenericEvent) {
        if (event is MessageReceivedEvent) {
            val m = event.member ?: return
            val ch = event.channel
            val msg = event.message
            if (!msg.contentDisplay.startsWith('!')) {
                return
            }
            val content = msg.contentDisplay.removePrefix("!").split(' ')
            if (content.isEmpty()) {
                return
            }
            val cmdAlias = content.first()
            val cmd = findCommand(cmdAlias) ?: return
            val args = if (content.size == 1) {
                emptyList()
            } else {
                content.subList(1, content.size)
            }
            GlobalScope.launch {
                cmd.execute(
                    CommandArguments(args),
                    UserSender(m, ch),
                    mirai
                )
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
        GlobalScope.launch {
            logger.info("Registering built in commands")
            register(VersionCommand())
            register(ListCommandsCommand())
            register(BotCommand())
            register(TodoCommand(mirai))
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
}