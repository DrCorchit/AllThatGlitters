package net.allthatglitters.server.appendices.armor

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.appendices.items.Item
import net.allthatglitters.server.util.html.HtmlObject

data class Armor(
	val name: String,
	val description: String,
	val bc: Int,
	val cost: Int,
	val weight: Int,
	val reqs: ImmutableSet<String>,
	val effects: ImmutableSet<String>
) {

	val item by lazy { Item(name, description, cost, weight) }

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

	companion object {
		fun deserialize(obj: JsonObject): Armor {
			return Armor(
				obj.get("name").asString,
				obj.get("description").asString,
				obj.get("bc").asInt,
				obj.get("cost")?.asInt ?: 0,
				obj.get("weight")?.asInt ?: 1,
				obj.get("reqs")?.asJsonArray?.map { it.asString }
					?.let { ImmutableSet.copyOf(it) } ?: ImmutableSet.of(),
				obj.get("effects")?.asJsonArray?.map { it.asString }
					?.let { ImmutableSet.copyOf(it) } ?: ImmutableSet.of()
			)
		}
	}
}