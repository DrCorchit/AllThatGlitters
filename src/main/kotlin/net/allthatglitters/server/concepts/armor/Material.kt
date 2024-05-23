package net.allthatglitters.server.concepts.armor

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.concepts.items.Item
import net.allthatglitters.server.util.html.HtmlObject
import kotlin.math.roundToInt

data class Material(
	val name: String,
	val description: String,
	val bc: Int,
	val costMult: Double,
	val weightMult: Double,
	val effects: ImmutableSet<String>
) {

	val item by lazy {
		Item(
			"$name Ingot",
			description,
			(costMult * 20).roundToInt(),
			(weightMult * 10).roundToInt()
		)
	}

	fun toRow(): HtmlObject {
		val row = HtmlObject("tr")
		row.withContent("td", name)
		row.withContent("td", bcString)
		row.withContent("td", costString)
		row.withContent("td", weightString)
		row.withContent("td", "$description ${effects.joinToString()}")
		return row
	}

	val bcString = String.format("%+d", bc)
	val costString = if (costMult < 1) {
		val inverse = (1 / costMult).roundToInt()
		"1/${inverse}x"
	} else {
		"${costMult.roundToInt()}x"
	}
	val weightString: String
		get() {
			val temp = (weightMult - 1) * 100
			return String.format("%+d%%", temp.roundToInt())
		}

	companion object {
		fun deserialize(obj: JsonObject): Material {
			return Material(
				obj.get("name").asString,
				obj.get("description").asString,
				obj.get("bc").asInt,
				obj.get("cost").asDouble,
				obj.get("weight").asDouble,
				obj.get("effects")?.asJsonArray?.map { it.asString }
					?.let { ImmutableSet.copyOf(it) } ?: ImmutableSet.of()
			)
		}
	}
}