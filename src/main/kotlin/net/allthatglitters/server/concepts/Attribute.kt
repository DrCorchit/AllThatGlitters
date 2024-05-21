package net.allthatglitters.server.concepts

import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

enum class Attribute(val fullName: String) {
	STR("Strength"),
	SPD("Speed"),
	DEX("Dexterity"),
	INT("Intelligence"),
	CHA("Charisma"),
	WILL("Willpower");

	companion object {
		fun parse(str: String): Attribute? {
			return entries.firstOrNull { it.name.equals(str, true) }
		}

		fun statBlockToHtml(stats: Map<Attribute, Int>): Renderable {
			val div = HtmlObject("div")
				.withClass("row")
				.withStyle("justify-content:start; margin: 0;")

			val left = HtmlObject("div").withClass("column-shrink")
			val center = HtmlObject("div").withClass("column-shrink")
			val right = HtmlObject("div").withClass("column-shrink")

			entries.zip(listOf(left, center, right, left, center, right))
				.forEach {
					val text = "${it.first.fullName}: ${stats[it.first]}"
					it.second.withContent("p", text)
				}

			return div.withContent(left).withContent(center).withContent(right)
		}
	}
}