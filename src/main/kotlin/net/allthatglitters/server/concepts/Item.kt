package net.allthatglitters.server.concepts

import com.drcorchit.justice.utils.json.JsonUtils
import com.google.gson.JsonObject
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

class Item(val name: String, val description: String, val cost: Int) : Renderable {

	override fun render(): String {
		val output = HtmlObject.background()
			.withStyle("margin: 5px; width: 200px; flex: 0 0 auto;")
		output.withContent("h5", name)
		output.withContent("p", description)
		val temp = "${"Cost".bold()}: $cost"
		output.withContent("p", temp)
		return output.render()
	}


	companion object {
		fun deserialize(obj: JsonObject): Item {
			return JsonUtils.GSON.fromJson(obj, Item::class.java)
		}
	}

}