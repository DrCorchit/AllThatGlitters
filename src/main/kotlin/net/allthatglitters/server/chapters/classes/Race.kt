package net.allthatglitters.server.chapters.classes

import com.google.gson.JsonObject
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable

class Race(val name: String, val description: List<String>, val bonuses: String) : Renderable {

	override fun render(): String {
		val output = HtmlContent()
		output.withContent(HtmlObject("h5").withContent(name))
		description.forEach { output.withContent(HtmlObject("p").withContent(it)) }
		output.withContent(HtmlObject("p").withContent(bonuses))
		return output.render()
	}

	companion object {
		fun deserialize(obj: JsonObject): Race {
			return Generator.deserializer.fromJson(obj, Race::class.java)
		}
	}
}