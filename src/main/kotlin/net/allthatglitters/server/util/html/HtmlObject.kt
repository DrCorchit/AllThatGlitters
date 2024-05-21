package net.allthatglitters.server.util.html

import com.google.gson.JsonObject
import net.allthatglitters.server.util.bold
import java.util.*

open class HtmlObject(
	val tag: String,
	val attributes: MutableMap<String, String> = TreeMap()
) : Renderable {

	private val content = HtmlContent()

	fun withClass(clazz: String): HtmlObject {
		return this.withAttribute("class", clazz)
	}

	fun withStyle(style: String): HtmlObject {
		return this.withAttribute("style", style)
	}

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

	fun withContent(tag: String, content: String): HtmlObject {
		return this.withContent(HtmlObject(tag).withContent(content))
	}

	fun withContent(content: Renderable): HtmlObject {
		this.content.withContent(content)
		return this
	}

	private fun renderAttributes(): String {
		return attributes.map { "${it.key}=\"${it.value}\"" }
			.joinToString(" ")
	}

	override fun render(): String {
		val builder = StringBuilder()
		val useNewlines = !newlineBlacklist.contains(tag)
		if (useNewlines) {
			builder.append("\n")
		}

		val renderedContent = content.render().let {
			if (useNewlines) {
				val tab = "  "
				tab + it.replace("\n", "\n$tab")
			} else it
		}

		builder.append(renderedContent)

		if (useNewlines) {
			builder.append("\n")
		}

		return if (attributes.isEmpty()) {
			"<$tag>$builder</$tag>"
		} else {
			"<$tag ${renderAttributes()}>$builder</$tag>"
		}
	}

	override fun toString(): String {
		return "<$tag ${renderAttributes()}>$content</$tag>"
	}

	companion object {
		val newlineBlacklist =
			setOf("p", "b", "i", "td", "th", "li", "h1", "h2", "h3", "h4", "h5", "h6")

		fun boldedEntry(tag: String, bold: String, content: String): HtmlObject {
			return HtmlObject(tag).withContent("${bold.bold()}: $content")
		}

		fun background(): HtmlObject {
			return HtmlObject("div").withClass("background")
		}

		fun flexBox(style: String = ""): HtmlObject {
			return HtmlObject("div")
				.withClass("row")
				.withStyle("justify-content:start; flex-wrap: wrap; margin: 0; $style")
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