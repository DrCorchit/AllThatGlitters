package net.allthatglitters.server.util

import java.util.TreeMap

class HtmlObject(val tag: String) {

    val attributes: MutableMap<String, String> = TreeMap()
    val content: StringBuilder = StringBuilder()

    fun withTag(key: String, value: String): HtmlObject {
        attributes[key] = value
        return this
    }

    fun hasContent(): Boolean {
        return content.isNotEmpty()
    }

    fun withContent(content: String): HtmlObject {
        this.content.append(content)
        return this
    }

    fun withContent(content: HtmlObject): HtmlObject {
        return withContent(content.render())
    }

    fun render(): String {
        if (attributes.isEmpty()) {
            return "<$tag>$content</$tag>"
        }
        val attrs = attributes.map { "${it.key}=\"${it.value}\"" }
            .joinToString(" ")
        return "<$tag $attrs>$content</$tag>"
    }
}