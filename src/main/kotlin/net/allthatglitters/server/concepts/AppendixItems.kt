package net.allthatglitters.server.concepts

import com.google.gson.JsonParser
import net.allthatglitters.server.inputDir
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import java.io.File

class AppendixItems(val rootFile: File = inputDir) :
	HtmlFile("Appendix: Mercantile Goods", "appendix_items.html") {

	//creatures, consumables, containers, miscellaneous, tools

	override fun appendBody(): HtmlFile {
		appendItems("Tools")
		appendItems("Consumables")
		appendItems("Containers")
		appendItems("Miscellaneous")

		appendElement("h4", "Creatures")
		val animals = HtmlObject.flexBox()
		getCreatures().forEach { animals.withContent(it) }
		//getCreatures().forEach { append(it) }
		append(animals)
		return this
	}

	private fun appendItems(name: String) {
		appendElement("h4", name)
		val items = HtmlObject.flexBox()
		getItems("items/${name.lowercase()}.json").forEach { items.withContent(it) }
		append(items)
	}

	fun getItems(filename: String): List<Item> {
		return File(rootFile, filename).readText()
			.let { JsonParser.parseString(it) }
			.asJsonArray
			.map { Item.deserialize(it.asJsonObject) }
			.sortedBy { it.name }
	}

	fun getCreatures(): List<Creature> {
		val creaturesFile = File(rootFile, "creatures/animals.json")
		if (creaturesFile.exists()) {
			return creaturesFile.readText().let { JsonParser.parseString(it) }
				.asJsonArray.map { Creature.deserialize(it.asJsonObject) }
		} else {
			println("Warning: file \"$creaturesFile\" not found")
			return listOf()
		}
	}


}