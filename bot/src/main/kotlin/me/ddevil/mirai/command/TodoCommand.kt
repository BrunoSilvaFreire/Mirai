package me.ddevil.mirai.command

import com.google.common.hash.Hashing
import me.ddevil.mirai.Mirai
import me.ddevil.mirai.persistence.set
import me.ddevil.util.exception.ArgumentOutOfRangeException
import me.ddevil.util.getString
import java.awt.Color
import javax.crypto.Cipher


class TodoCommand(mirai: Mirai) : ScopedCommand("todo", "Salva todos os itens que devem", "asd", mirai) {
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
                        raw("**TODO: '${item.content}'**")
                        raw(item.hash)
                    }
                }
            }

        }
        register("add") { args, sender, mirai ->
            val parts = args.arguments
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
                raw("TODO: '${todo.content}'")
            }
        }
        register("remove") { args, sender, mirai ->
            try {
                val hash = args.getString(0)
                if (
                    items.removeAll {
                        it.hash == hash
                    }
                ) {
                    persistence.delete(hash)
                    sender.reply {
                        color = Color.CYAN
                        title = "TODO(s) removidos"
                        raw("TODO(s) com hash **$hash** removido(s)")
                    }
                } else {
                    sender.reply {
                        markError()
                        title = "TODO não encontrado"
                        raw("Não existe um TODO com a hash **$hash**")
                    }
                }
            } catch (e: ArgumentOutOfRangeException) {
                sender.reply {
                    markError()
                    title = "Hash não encontrada"
                    raw("Você precisa informar uma hash!")
                }
            }
        }
    }
}