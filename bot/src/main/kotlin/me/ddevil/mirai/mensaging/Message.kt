package me.ddevil.mirai.mensaging

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
            val msg =text
            if(bold){
            }
            return msg
        }

}

class Message(
    val components: MutableList<MessageComponent> = ArrayList()
) {
    fun raw(text: String) {
        components += RawMessageComponent(text)
    }

    fun markdown(
        content: String,
        bold: Boolean = false
    ) {

    }

    val content
        get() = components.joinToString(separator = System.lineSeparator()) {
            it.content
        }
}
