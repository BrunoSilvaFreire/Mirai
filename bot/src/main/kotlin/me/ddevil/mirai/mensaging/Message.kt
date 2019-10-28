package me.ddevil.mirai.mensaging

import java.awt.Color

interface MessageComponent {
    val content: String

}

class RawMessageComponent(
    override val content: String
) : MessageComponent

class MarkdownComponent(
    val text: String,
    val bold: Boolean = false
) : MessageComponent {
    override val content: String
        get() {
            val msg = text
            if (bold) {
            }
            return msg
        }

}

class Message(
    val components: MutableList<MessageComponent> = ArrayList(),
    var title: String? = null,
    var color: Color? = null

) {
    fun markError() {
        color = Color.RED
    }

    fun raw(text: String) {
        components += RawMessageComponent(text)
    }

    fun markdown(
        content: String,
        bold: Boolean = false
    ) {

    }

    fun isRich(): Boolean {
        return title != null || color != null
    }

    val content
        get() = components.joinToString(separator = System.lineSeparator()) {
            it.content
        }
}
