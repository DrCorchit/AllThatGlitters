package net.allthatglitters.server.util.html

import com.google.gson.JsonObject
import net.allthatglitters.server.util.bold
import java.util.*

open class HtmlObject(
    val tag: String,
    val attributes: MutableMap<String, String> = TreeMap()
) : Renderable {

    private val content = HtmlContent()

    fun withAttribute(key: String, value: String): HtmlObject {
        attributes[key] = value
        return this
    }

    fun hasContent(): Boolean {
        return content.hasContent()
    }

    fun withBoldedEntry(bold: String, content: String): HtmlObject {
        val entryTag = when (tag) {
            "ul", "ol" -> "li"
            else -> "p"
        }
        return withContent(boldedEntry(entryTag, bold, content))
    }

    fun withAll(html: Iterable<Renderable>): HtmlObject {
        content.withAll(html)
        return this
    }

    fun withContent(content: String): HtmlObject {
        return withContent(HtmlString(content))
    }

    fun withContent(content: Renderable): HtmlObject {
        this.content.withContent(content)
        return this
    }

    //val newlineWhitelist = setOf("html", "head", "body", "ol", "ul", "table")
    val newlineBlacklist = setOf("p", "b", "i", "td", "th", "li", "h1", "h2", "h3", "h4", "h5", "h6")

    private fun renderAttributes(): String {
        return attributes.map { "${it.key}=\"${it.value}\"" }
            .joinToString(" ")
    }

    override fun render(): String {

        val temp = StringBuilder()
        val useNewlines = !newlineBlacklist.contains(tag)
        if (useNewlines) {
            temp.append("\n")
        }

        temp.append(content.render())

        if (useNewlines) {
            temp.append("\n")
        }

        if (attributes.isEmpty()) {
            return "<$tag>$temp</$tag>"
        }
        return "<$tag ${renderAttributes()}>$temp</$tag>"
    }

    override fun toString(): String {
        return "<$tag ${renderAttributes()}>...</$tag>"
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