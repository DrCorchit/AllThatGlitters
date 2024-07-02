package net.allthatglitters.server.chapters.sheet

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.appendices.weapons.Keyword
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

data class Skill(
	val name: String,
	override val description: String,
	val attrs: ImmutableSet<Attribute>
) : HasProperties, Keyword {
	override val displayName = name

	fun toRow(): Renderable {
		val output = HtmlObject("tr")
			.withContent(
				HtmlObject("td")
					.withClass("noborder")
					.withContent(name)
					.withContent(
						HtmlObject("span")
							.withStyle("color: #888; float: right;")
							.withContent(attrs.joinToString("/", "(", ")") { it.abbr })
					)
			)
			.withContent("td", "")//"____")
			.withContent("td", "+")//"+________")
			.withContent("td", "=")//""=_____")
		return output
	}

	override val abbr = null

	override fun getProperty(property: String): Any? {
		return when (property) {
			"name" -> name
			"description" -> description
			"attrs" -> attrs.joinToString()
			else -> null
		}
	}

	override fun toString(): String {
		return name
	}

	companion object : HasProperties {
		fun deserialize(obj: JsonObject): Skill {
			return Skill(obj.get("name").asString,
				obj.get("description").asString,
				obj.getAsJsonArray("attrs")
					.map { Attribute.parse(it.asString) }
					.let { ImmutableSet.copyOf(it) })
		}

		fun parse(str: String): Skill {
			return Sheet.skills[str.normalize()]
				?: throw NoSuchElementException("No such skill: $str")
		}

		override fun getProperty(property: String): Any {
			return parse(property)
		}
	}
}