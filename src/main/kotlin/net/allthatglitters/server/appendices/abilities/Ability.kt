package net.allthatglitters.server.appendices.abilities

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

data class Ability(
	val name: String,
	val effect: String,
	val slots: Int,
	val gold: Int,
	val reqs: ImmutableSet<String>
) : Renderable, HasProperties {

	override fun render(): String {
		val output = HtmlObject.background()
		val link = HtmlObject("a").withAttribute("id", name.normalize())
		output.withContent("h5", link.render() + name)
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

	fun linkTo(text: String = name): HtmlObject {
		return HtmlObject("a")
			.withAttribute("href", "${AppendixAbilities.outputFile}#${name.normalize()}")
			.withContent(text)
	}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"name" -> name
			else -> null
		}
	}

	companion object : HasProperties {
		fun deserialize(obj: JsonObject): Ability {
			val name = obj.get("name").asString
			val effect = obj.get("effect").asString
			val cost = obj.getAsJsonObject("cost")
			val slots = cost.get("slots")?.asInt ?: 0
			val gold = cost.get("gold")?.asInt ?: 0
			val reqs = obj.getAsJsonArray("requirements")
				?.map { it.asString }
				?.let { ImmutableSet.copyOf(it) } ?: ImmutableSet.of()
			return Ability(name, effect, slots, gold, reqs)
		}

		override fun getProperty(property: String): Any {
			return AppendixAbilities.lookupAbility(property)
		}
	}
}