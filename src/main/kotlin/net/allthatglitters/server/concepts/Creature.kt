package net.allthatglitters.server.concepts

import com.google.common.collect.ImmutableMap
import com.google.gson.JsonObject
import net.allthatglitters.server.concepts.Attribute.Companion.statBlockToHtml
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

class Creature(
	val name: String,
	val description: String,
	val cost: Int,
	val upkeep: Int,
	val stats: ImmutableMap<Attribute, Int>
) : Renderable {
	override fun render(): String {
		val output = HtmlObject.background()
			.withStyle("margin: 5px; width: 300px; flex: 1 0 auto;")
		output.withContent("h5", name)
		output.withContent("p", description)
		val temp = "${"Cost".bold()}: $cost" +
				if (upkeep > 0) " ${"Daily Upkeep".bold()}: $upkeep"
				else 0
		output.withContent("p", temp)
		output.withContent(statBlockToHtml(stats))
		return output.render()
	}

	companion object {
		fun deserialize(obj: JsonObject): Creature {
			return Creature(
				obj.get("name").asString,
				obj.get("description").asString,
				obj.get("cost")?.asInt ?: 0,
				obj.get("upkeep")?.asInt ?: 0,
				obj.getAsJsonObject("stats").entrySet()
					.associate { Attribute.parse(it.key) to it.value.asInt }
					.let { ImmutableMap.copyOf(it) }
			)
		}
	}
}