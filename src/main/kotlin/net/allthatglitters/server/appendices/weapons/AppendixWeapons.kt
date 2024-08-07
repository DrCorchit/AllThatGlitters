package net.allthatglitters.server.appendices.weapons

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.JsonParser
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object AppendixWeapons : HtmlFile(
	"Appendix: Weapons",
	"appendix_weapons.html",
	File(generator.inputDir, "appendices/3_weapons")
) {
	private val headers =
		arrayOf("Name", "Damage", "Price", "Modifiers", "Requirements", "Notes")

	val keywords = WeaponKeyword.entries.associateBy { it.name.normalize() }

	fun lookupModifier(name: String): Keyword {
		return keywords[name.normalize()]
			?: throw NoSuchElementException("No such modifier: $name")
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
		}.associateBy { it.name.normalize() }

	val weapons = weaponTables.values.flatMap { it.weapons }
		.associateBy { it.name.normalize() }

	fun lookupWeapon(name: String): Weapon {
		return weapons[name.normalize()] ?: throw NoSuchElementException("No such weapon: $name")
	}

	override fun appendBody(): HtmlFile {

		appendElement("h4", "Weapon Modifiers")
		appendElement("p", "Certain weapons have special properties, which affect how they behave:")
		val list = HtmlObject("ul")
			.withAll(keywords.values.map {
				HtmlObject("li").withContent("${it.displayName.bold()}: ${it.description}")
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