package net.allthatglitters.server.concepts.abilities

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

data class Training(
	val name: String,
	val effect: String,
	val slots: Int,
	val gold: Int,
	val reqs: ImmutableSet<String>
) : Renderable {

	override fun render(): String {
		val output = HtmlObject.background()
		output.withContent("h6", name)
		if (reqs.isNotEmpty()) {
			output.withContent("p", "Requirements: " + reqs.joinToString())
		}
		val cost = StringBuilder()
		if (slots > 0) cost.append("<b>Training Slots</b>: $slots ")
		if (gold > 0) cost.append("<b>Gold</b>: $gold")
		output.withContent("p", cost.toString())
		output.withContent("p", effect)
		return output.render()
	}

	companion object {
		fun deserialize(obj: JsonObject): Training {
			val name = obj.get("name").asString
			val effect = obj.get("effect").asString
			val cost = obj.getAsJsonObject("cost")
			val slots = cost.get("slots")?.asInt ?: 0
			val gold = cost.get("gold")?.asInt ?: 0
			val reqs = obj.getAsJsonArray("requirements")
				?.map { it.asString }
				?.let { ImmutableSet.copyOf(it) } ?: ImmutableSet.of()
			return Training(name, effect, slots, gold, reqs)
		}
	}
}