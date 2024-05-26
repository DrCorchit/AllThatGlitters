package net.allthatglitters.server.chapters.sheet

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

data class Skill(val name: String, val description: String, val attrs: ImmutableSet<Attribute>) {

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
			.withContent("td", "<br />")
			.withContent("td", "+")
			.withContent("td", "=")
		return output
	}

	companion object {
		fun deserialize(obj: JsonObject): Skill {
			return Skill(obj.get("name").asString,
				obj.get("description").asString,
				obj.getAsJsonArray("attrs")
					.map { Attribute.parse(it.asString)!! }
					.let { ImmutableSet.copyOf(it) })
		}

		fun parse(name: String): Skill {
			return Sheet.skills[name.uppercase()]!!
		}
	}
}