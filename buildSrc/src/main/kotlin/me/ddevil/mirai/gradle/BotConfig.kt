package me.ddevil.mirai.gradle

data class BotConfig(
    val botId: String,
    val key: String,
    var commandPrefix: Char? = null
)