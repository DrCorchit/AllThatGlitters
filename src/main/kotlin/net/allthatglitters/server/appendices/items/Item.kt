package net.allthatglitters.server.appendices.items

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.JsonObject
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable
import net.allthatglitters.server.util.makeTooltip
import net.allthatglitters.server.util.underline

class Item(
	val name: String,
	val description: String,
	val cost: Int,
	val weight: Int,
	val key: String = name.normalize()
) : Renderable {

	override fun render(): String {
		val output = HtmlObject.background()
			.withStyle("margin: 5px; width: 200px; flex: 0 0 auto;")
		output.withContent("h5", name)
		output.withContent("p", description)
		val temp = "<b>Price</b>: $cost" + if (weight > 0) {
			" <b>Weight</b>: $weight lbs"
		} else ""
		output.withContent("p", temp)
		return output.render()
	}

	fun toTooltip(): Renderable {
		return makeTooltip(name.lowercase(), description)
	}

	override fun toString(): String {
		//return "Item($name)"
		return "items.$key"
	}

	companion object {
		fun deserialize(obj: JsonObject): Item {
			if (!obj.has("key")) {
				obj.addProperty("key", obj.get("name").asString.normalize())
			}
			return Generator.deserializer.fromJson(obj, Item::class.java)
		}
	}

}