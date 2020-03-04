package me.zortex.mirai.dj.command

import me.ddevil.mirai.Mirai
import me.ddevil.mirai.command.Command
import me.ddevil.mirai.command.CommandArguments
import me.ddevil.mirai.command.CommandSender
import me.zortex.mirai.dj.DJ
import java.lang.NumberFormatException

class VolumeCommand(
    val dj: DJ
) : Command("volume", "Changes the music player volume", "volume (number)", dj) {
    override suspend fun onExecute(args: CommandArguments, sender: CommandSender, mirai: Mirai) {
        if (args.length == 0) {
            sender.reply {
                markError()
                raw("You need to provide a new volume.")
            }
            return
        }
        try {
            val volume = args.getFloat(0)
            dj.volume = volume
            sender.reply {
                raw("Volume changed to $volume")
            }
        } catch (ex: NumberFormatException) {
            sender.reply {
                markError()
                raw("Unable to parse \"${args.getString(0)}\" as a number.")
            }
        }
    }
}