package me.ddevil.mirai.command

import com.google.common.hash.Hashing
import me.ddevil.mirai.Mirai
import me.ddevil.mirai.persistence.set
import me.ddevil.util.getString
import javax.crypto.Cipher


class TodoCommand(mirai: Mirai) : ScopedCommand("todo", "Salva todos os itens que devem", "asd") {
    val persistence = mirai.persistenceManager.request("cmd.todo")
    val items = ArrayList<Todo>()

    data class Todo(
        val hash: String,
        val content: String
    )

    override suspend fun onInitialize() {
        items.addAll(persistence.all().map {
            Todo(
                it.getString("hash"),
                it.getString("content")
            )
        })
    }

    init {
        register("list") { args, sender, mirai ->
            sender.reply {
                if (items.isEmpty()) {
                    raw("Sem TODO's")
                } else {
                    for ((index, item) in items.withIndex()) {
                        raw("**TODO: '$item'**")
                        raw(item.hash)
                    }
                }
            }

        }
        register("add") { args, sender, mirai ->
            val parts = args.args
            if (parts.isEmpty()) {
                sender.reply {
                    raw("Você deve descrever um item!")
                }
                return@register
            }
            val str = parts.joinToString(separator = " ")
            val hash = Hashing.sha256().hashString(str, Charsets.UTF_8).toString()
            val todo = Todo(
                hash,
                str
            )
            persistence.set(hash) {
                this["hash"] = hash
                this["content"] = str
            }
            items.add(todo)
            sender.reply {
                raw("Adicionado TODO:")
                raw("TODO: '$todo'")
            }
        }
        register("remove") { args, sender, mirai ->

        }
    }
}