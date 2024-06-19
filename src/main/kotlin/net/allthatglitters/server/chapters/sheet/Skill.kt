package net.allthatglitters.server.chapters.sheet

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.Tooltip
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable
import net.allthatglitters.server.util.makeTooltip

data class Skill(val name: String, val description: String, val attrs: ImmutableSet<Attribute>): HasProperties, Tooltip {
	override val defaultText = name
	override val value = description

	fun toTooltip(): Renderable {
		return makeTooltip(name, description)
	}

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

	companion object : HasProperties {
		fun deserialize(obj: JsonObject): Skill {
			return Skill(obj.get("name").asString,
				obj.get("description").asString,
				obj.getAsJsonArray("attrs")
					.map { Attribute.parse(it.asString) }
					.let { ImmutableSet.copyOf(it) })
		}

		fun parse(str: String): Skill {
			return Sheet.skills[str.normalize()] ?: throw NoSuchElementException("No such skill: $str")
		}

		override fun getProperty(property: String): Any {
			return parse(property)
		}
	}

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
}