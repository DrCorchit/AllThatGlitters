package net.allthatglitters.server.util.html

class HtmlString(val content: String): Renderable {

    override fun render(): String {
        return content
    }

    override fun toString(): String {
        return content
    }
}