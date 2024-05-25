package net.allthatglitters.server.concepts.bestiary

import com.google.common.collect.ImmutableMap
import com.google.gson.JsonObject
import net.allthatglitters.server.concepts.items.Item
import net.allthatglitters.server.concepts.sheet.Attribute
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

class Creature(
	obj: JsonObject
) : Renderable {
	val name = obj.get("name").asString
	val description = obj.get("description").asString
	//val species = AppendixBestiary.lookupSpecies(obj.get("species").asString)
	val cost: Int = obj.get("cost")?.asInt ?: 0
	val upkeep: Int = obj.get("upkeep")?.asInt ?: 0
	val weight: Int = obj.get("weight")?.asInt ?: 1
	val stats: ImmutableMap<Attribute, Int> = obj.getAsJsonObject("stats").entrySet()
		.associate { Attribute.parse(it.key) to it.value.asInt }
		.let { ImmutableMap.copyOf(it) }

	val item by lazy {
		Item(name, description, cost, weight)
	}

	override fun render(): String {
		val output = HtmlObject.background()
			.withStyle("margin: 5px; width: 300px; flex: 1 0 auto;")
		output.withContent("h5", name)
		output.withContent("p", description)
		val temp = "${"Cost".bold()}: $cost" +
				if (upkeep > 0) " ${"Daily Upkeep".bold()}: $upkeep"
				else 0
		output.withContent("p", temp)
		output.withContent(Attribute.statBlockToHtml(stats))
		return output.render()
	}
}