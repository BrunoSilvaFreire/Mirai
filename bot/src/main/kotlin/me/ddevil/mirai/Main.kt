package me.ddevil.mirai

fun main(args: Array<String>) {
    val token = args.joinToString("")
    val mirai = Mirai(token)
    mirai.run()
}