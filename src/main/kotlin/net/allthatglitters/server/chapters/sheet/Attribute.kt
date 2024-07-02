package net.allthatglitters.server.chapters.sheet

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.JsonObject
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.appendices.weapons.Keyword
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

data class Attribute(
	override val abbr: String,
	val name: String,
	override val description: String,
	val interpretation: String?,
	val effects: Set<String>
) : Renderable, HasProperties, Keyword {
	override val displayName get() = name

	override fun render(): String {
		return abbr.bold()
	}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"abbr" -> abbr
			"name" -> displayName
			"description" -> description
			"effects" -> effects.joinToString()
			else -> null
		}
	}

	override fun toString(): String {
		return displayName
	}

	companion object : HasProperties {
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
					val text = "${it.first.displayName}: ${stats[it.first]}"
					it.second.withContent("p", text)
				}

			return div.withContent(left).withContent(center).withContent(right)
		}

		override fun getProperty(property: String): Any {
			return parse(property)
		}
	}
}