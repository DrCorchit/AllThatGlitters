package net.allthatglitters.server.appendices.items

import net.allthatglitters.server.Generator
import net.allthatglitters.server.appendices.armor.AppendixArmor
import net.allthatglitters.server.appendices.bestiary.AppendixBestiary
import net.allthatglitters.server.appendices.weapons.AppendixWeapons
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import java.io.File

object AppendixItems : HtmlFile("Appendix: Mercantile Goods", "appendix_items.html") {

	override val inputDir = File(Generator.inputDir, "items")

	override fun appendBody(): HtmlFile {
		appendItems("Tools")
		appendItems("Consumables")
		appendItems("Containers")
		appendItems("Weapons", getWeapons())
		appendItems("Armor", getArmor())
		appendItems("Materials", getMaterials())
		appendItems("Animals", getAnimals())
		appendItems("Miscellaneous")


		appendElement("h4", "Creatures")
		val animals = HtmlObject.flexBox()
		//getCreatures().forEach { animals.withContent(it) }
		//getCreatures().forEach { append(it) }
		append(animals)
		return this
	}

	private fun appendItems(name: String, list: List<Item> = getItems("${name.lowercase()}.json")) {
		appendElement("h4", name)
		val items = HtmlObject.flexBox()
		list.sortedBy { it.name }.forEach { items.withContent(it) }
		append(items)
	}

	private fun getItems(filename: String): List<Item> {
		return File(inputDir, filename).deserialize { Item.deserialize(it) }
	}

	private fun getWeapons(): List<Item> {
		return AppendixWeapons.weaponTables["Small Weapons"]!!.weapons.map { it.item }
	}

	private fun getArmor(): List<Item> {
		return AppendixArmor.armor.map { it.item }
	}

	private fun getMaterials(): List<Item> {
		return AppendixArmor.materials.map { it.item }
	}

	private fun getAnimals(): List<Item> {
		return AppendixBestiary.creatures.map { it.item }
	}

	/*
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
	 */
}