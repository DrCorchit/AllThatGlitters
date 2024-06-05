package net.allthatglitters.server.chapters.sheet

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.JsonObject
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.Tooltip
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlString
import net.allthatglitters.server.util.html.Renderable

data class Attribute(
	val name: String,
	val abbr: String,
	val description: String,
	val interpretation: String?,
	val effects: Set<String>
) : Renderable, Tooltip {
	override val defaultText = abbr
	override val value = description

	override fun render(): String {
		val list = HtmlObject("ul").withAll(effects.map { HtmlString(it).wrap("li") })

		val output = HtmlContent()
			.withContent(HtmlObject("h5").withContent(name))
			.withContent(HtmlObject("p").withContent(description))
			.withContent(HtmlObject("p").withContent("Gameplay Effects:"))
			.withContent(list)
		if (interpretation != null) {
			output.withContent(HtmlObject("p").withContent("Real-life Equivalence: $interpretation"))
		}
		return output.render()
	}

	override fun toString(): String {
		return name
	}

	companion object {
		val STR by lazy { parse("STR") }
		val DEX by lazy { parse("DEX") }
		val SPD by lazy { parse("SPD") }
		val INT by lazy { parse("INT") }
		val NST by lazy { parse("NST") }
		val CHA by lazy { parse("CHA") }

		val regex = Sheet.attributes.values
			.joinToString("|", "(", ")") { it.abbr }

		fun deserialize(obj: JsonObject): Attribute {
			return generator.deserializer.fromJson(obj, Attribute::class.java)
		}

		fun parse(str: String): Attribute {
			return Sheet.attributes[str.normalize()]
				?: throw NoSuchElementException("No such attribute: $str")
		}

		fun statBlockToHtml(stats: Map<Attribute, Int>): Renderable {
			val div = HtmlObject("div")
				.withClass("row")
				.withStyle("justify-content:start; margin: 0;")

			val left = HtmlObject("div").withClass("column-shrink")
			val center = HtmlObject("div").withClass("column-shrink")
			val right = HtmlObject("div").withClass("column-shrink")

			Sheet.attributes.values.zip(listOf(left, center, right, left, center, right))
				.forEach {
					val text = "${it.first.name}: ${stats[it.first]}"
					it.second.withContent("p", text)
				}

			return div.withContent(left).withContent(center).withContent(right)
		}
	}
}