package net.allthatglitters.server.appendices.armor

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.allthatglitters.server.Generator
import net.allthatglitters.server.appendices.items.Item
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.underline

data class Armor(
	val name: String,
	val description: String,
	val bc: Int,
	val cost: Int,
	val weight: Int,
	val reqs: Set<String>,
	val effects: Set<String>,
	val key: String = name.normalize()
) {
	val item get() = Item(name, description, cost, weight, key)

	fun toRow(): HtmlObject {
		val row = HtmlObject("tr")
		row.withContent("td", name)
		row.withContent("td", bc.toString())
		row.withContent("td", cost.toString())
		row.withContent("td", weight.toString())
		row.withContent("td", reqs.joinToString())
		row.withContent("td", "$description ${effects.joinToString()}")
		return row
	}

	fun toTooltip(): HtmlObject {
		return HtmlObject("span")
			.withAttribute("data-tooltip", "$description BC: $bc")
			.withContent(name.lowercase().underline())
	}

	companion object {
		fun deserialize(obj: JsonObject): Armor {
			if (!obj.has("key")) {
				obj.addProperty("key", obj.get("name").asString.normalize())
			}
			if (!obj.has("reqs")) {
				obj.add("reqs", JsonArray())
			}
			if (!obj.has("effects")) {
				obj.add("effects", JsonArray())
			}

			return Generator.deserializer.fromJson(obj, Armor::class.java)
		}
	}
}