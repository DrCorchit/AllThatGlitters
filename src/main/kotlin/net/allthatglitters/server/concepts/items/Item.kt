package net.allthatglitters.server.concepts.items

import com.google.gson.JsonObject
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

class Item(val name: String, val description: String, val cost: Int, val weight: Int) : Renderable {

	override fun render(): String {
		val output = HtmlObject.background()
			.withStyle("margin: 5px; width: 200px; flex: 0 0 auto;")
		output.withContent("h6", name)
		output.withContent("p", description)
		val temp = "<b>Price</b>: $cost" + if (weight > 0) {
			" <b>Weight</b>: $weight lbs"
		} else ""
		output.withContent("p", temp)
		return output.render()
	}


	companion object {
		fun deserialize(obj: JsonObject): Item {
			return Generator.deserializer.fromJson(obj, Item::class.java)
		}
	}

}