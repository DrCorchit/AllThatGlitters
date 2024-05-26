package net.allthatglitters.server.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.allthatglitters.server.chapters.sheet.Sheet.inputDir
import net.allthatglitters.server.chapters.sheet.Sheet.skills
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
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

private val regex = "\\{\\{(.*)}}".toRegex()
fun replaceValuesInTemplate(template: String): String {
	return regex.replace(template) { matchResult ->
		when (matchResult.value) {
			"5_skills.html" -> {
				val table = HtmlTable().withClass("inner")
				skills.values.map { table.withContent(it.toRow()) }
				HtmlObject("td")
					.withClass("border")
					.withAttribute("colspan", "3")
					.withContent(table).render()
				"Hello"
			}
			else -> File(inputDir, matchResult.groupValues[1]).readText()
		}
	}
}