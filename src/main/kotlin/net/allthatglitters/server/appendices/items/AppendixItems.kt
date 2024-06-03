package net.allthatglitters.server.appendices.items

import com.drcorchit.justice.utils.StringUtils.normalize
import net.allthatglitters.server.Generator
import net.allthatglitters.server.appendices.armor.AppendixArmor
import net.allthatglitters.server.appendices.bestiary.AppendixBestiary
import net.allthatglitters.server.appendices.weapons.AppendixWeapons
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import java.io.File

object AppendixItems : HtmlFile("Appendix: Mercantile Goods", "appendix_items.html") {
	override val inputDir = File(Generator.inputDir, "appendices/5_items")
	val itemCategories = mapOf(
		"Tools" to getItems("tools.json"),
		"Clothing" to getItems("clothing.json"),
		"Consumables" to getItems("consumables.json"),
		"Armor" to getArmor(),
		"Weapons" to getWeapons(),
		"Materials" to getMaterials(),
		"Animals" to getAnimals(),
		"Containers" to getItems("containers.json"),
		"Miscellaneous" to getItems("miscellaneous.json")
	)

	val items by lazy {
		itemCategories.values.flatten().associateBy { it.key }
	}

	fun lookupItem(name: String): Item {
		return items[name.normalize()] ?: throw NoSuchElementException("No such item: $name")
	}

	override fun appendBody(): HtmlFile {
		itemCategories.forEach { (category, items) -> appendItems(category, items) }
		return this
	}

	private fun appendItems(name: String, list: List<Item>) {
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
		return AppendixArmor.armor.values.map { it.item }
	}

	private fun getMaterials(): List<Item> {
		return AppendixArmor.materials.map { it.item }
	}

	private fun getAnimals(): List<Item> {
		return AppendixBestiary.creatures.map { it.item }
	}
}