package net.allthatglitters.server.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.allthatglitters.server.util.html.HtmlObject
import java.io.File


fun String.bold(): String {
	return HtmlObject("b").withContent(this).render()
}

fun String.italicise(): String {
	return HtmlObject("i").withContent(this).render()
}

fun <T> File.deserialize(deserializer: (JsonObject) -> T): List<T> {
	return this.readText().let { JsonParser.parseString(it) }
		.asJsonArray.map {
		try {
			deserializer.invoke(it.asJsonObject)
		} catch (e: Exception) {
			val name = it.asJsonObject.get("name")?.asString
			throw IllegalArgumentException("Error deserializing file $this: $name", e)
		}
	}
}