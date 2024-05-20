package net.allthatglitters.server.util.html

import com.google.gson.JsonObject
import net.allthatglitters.server.util.bold
import java.util.*

open class HtmlObject(val tag: String, val attributes: MutableMap<String, String> = TreeMap()) :
    Renderable {

    private val content: StringBuilder = StringBuilder()

    fun withAttribute(key: String, value: String): HtmlObject {
        attributes[key] = value
        return this
    }

    fun hasContent(): Boolean {
        return content.isNotEmpty()
    }

    fun withBoldedEntry(bold: String, content: String): HtmlObject {
        val entryTag = when (tag) {
            "ul", "ol" -> "li"
            else -> "p"
        }
        return withContent(boldedEntry(entryTag, bold, content))
    }

    fun withAll(content: Iterable<Renderable>): HtmlObject {
        return withContent(content.joinToString("\n") { it.render() })
    }

    fun withContent(content: String): HtmlObject {
        this.content.append(content)
        return this
    }

    fun withContent(content: Renderable): HtmlObject {
        return withContent(content.render())
    }

    override fun render(): String {
        if (attributes.isEmpty()) {
            return "<$tag>$content</$tag>"
        }
        val attrs = attributes.map { "${it.key}=\"${it.value}\"" }
            .joinToString(" ")
        return "<$tag $attrs>$content</$tag>"
    }

    override fun toString(): String {
        return render()
    }

    companion object {
        fun boldedEntry(tag: String, bold: String, content: String): HtmlObject {
            return HtmlObject(tag).withContent("${bold.bold()}: $content")
        }

        fun deserialize(obj: JsonObject): HtmlObject {
            val tag = obj.get("tag").asString
            val attributes = obj.getAsJsonObject("attributes")?.let {
                it.entrySet().associate { attribute -> attribute.key to attribute.value.asString }
            }?.toMutableMap() ?: TreeMap()
            obj.get("style")?.let { attributes["style"] = it.asString }
            obj.get("class")?.let { attributes["class"] = it.asString }
            val content = obj.get("content")?.let { Renderable.deserialize(it) } ?: HtmlContent()
            return HtmlObject(tag, attributes).withContent(content)

        }
    }
}