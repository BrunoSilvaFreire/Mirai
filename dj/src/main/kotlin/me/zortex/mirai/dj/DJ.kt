package me.zortex.mirai.dj

import kotlinx.coroutines.runBlocking
import me.ddevil.mirai.command.DebugCommand
import me.ddevil.mirai.command.Prefixed
import me.ddevil.mirai.plugins.Plugin;
import me.zortex.mirai.dj.command.VolumeCommand

class DJ : Plugin(), Prefixed {
    override fun bootstrap() {
        // Create a new coroutineScope so that we can call suspend functions
        runBlocking {
            //"with" calls the given block of code with the provided argument as "this"
            // If you don't know what I'm talking about this is your friend: https://kotlinlang.org/docs/reference/lambdas.html
            with(mirai.commandManager) {
                // Register VolumeCommand onto CommandManager
                register(VolumeCommand(this@DJ))
                // Find DebugCommand and register debugging extensions for DJ plugin
                withCommandOf<DebugCommand> {
                    register(this@DJ) {
                        raw("Volume: $volume")
                    }
                }
            }
        }
    }

    var volume: Float = 1.0F
    override val prefix: String
        get() = "music"
}