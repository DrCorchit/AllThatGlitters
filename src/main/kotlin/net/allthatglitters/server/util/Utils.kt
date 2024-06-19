package net.allthatglitters.server.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable
import java.io.File


fun String.bold(): String {
	return HtmlObject("b").withContent(this).render()
}

fun String.underline(): String {
	return HtmlObject("u").withContent(this).render()
}

fun String.italicise(): String {
	return HtmlObject("i").withContent(this).render()
}

fun <T> File.deserialize(deserializer: (JsonObject) -> T): List<T> {
	return this.readText().let {
		try {
			JsonParser.parseString(it)
		} catch (e: Exception) {
			throw IllegalArgumentException("Error parsing file $this (invalid json)", e)
		}
	}.asJsonArray.map {
		try {
			deserializer.invoke(it.asJsonObject)
		} catch (e: Exception) {
			val name = it.asJsonObject.get("name")?.asString
			throw IllegalArgumentException("Error deserializing file $this: $name", e)
		}
	}
}

fun makeTooltip(text: String, tooltip: String): Renderable {
	return HtmlObject("span")
		.withAttribute("data-tooltip", tooltip)
		.withContent(text.underline())
}

fun <K2, V2, K1 : K2, V1 : V2> Map<K1, V1>.inherit(parent: Map<K2, V2>): Map<K2, V2> {
	return (this.keys + parent.keys).associateWith { this.getOrDefault(it, parent[it])!! }
}

