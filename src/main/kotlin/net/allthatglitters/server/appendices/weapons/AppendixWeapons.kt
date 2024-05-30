package net.allthatglitters.server.appendices.weapons

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.JsonParser
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object AppendixWeapons : HtmlFile("Appendix: Weapons", "appendix_weapons.html") {
	override val inputDir = File(Generator.inputDir, "weapons")
	private val headers =
		arrayOf("Name", "Damage", "Price", "Modifiers", "Requirements", "Notes")

	val modifiers =
		(EnumModifier.entries.map { it.displayName to it.description } +
				ValueModifier.Type.entries.map { it.displayName to it.description })
			.associateBy { it.first.normalize() }

	fun lookupModifier(name: String): Pair<String, String> {
		return modifiers[name.normalize()] ?: throw NoSuchElementException("No such modifier: $name")
	}

	val weaponTables = File(inputDir, "weaponTables.json")
		.readText()
		.let { JsonParser.parseString(it) }
		.asJsonArray.map { it.asJsonObject }
		.map {
			WeaponsTable(
				it.get("name").asString,
				it.get("description").asString,
				File(inputDir, it.get("file").asString)
			)
		}.associateBy { it.name }

	val weapons = weaponTables.values.flatMap { it.weapons }
		.associateBy { it.name.normalize() }

	fun lookupWeapon(name: String): Weapon {
		return weapons[name.normalize()] ?: throw NoSuchElementException("No such weapon: $name")
	}

	override fun appendBody(): HtmlFile {

		appendElement("h4", "Weapon Modifiers")
		appendElement("p", "Certain weapons have special properties, which affect how they behave:")
		val list = HtmlObject("ul")
			.withAll(modifiers.entries.map {
				HtmlObject("li").withContent("<b>${it.value.first}</b>: ${it.value.second}</li>")
			})
		append(list)

		weaponTables.values.forEach {
			appendElement("h4", it.name)
			appendElement("p", it.description)
			val table = HtmlTable()
			table.withHeaders(*headers)
			it.weapons.forEach { weapon -> table.withContent(weapon) }
			append(table)
		}

		return this
	}

	data class WeaponsTable(val name: String, val description: String, val weaponsFile: File) {
		val weapons = weaponsFile.readText().let { JsonParser.parseString(it) }
			.asJsonArray.map {
				try {
					Weapon.deserialize(it.asJsonObject)
				} catch (e: Exception) {
					val name = it.asJsonObject.get("name")
					throw IllegalArgumentException("Could not deserialize weapon: $name", e)
				}
			}
	}
}