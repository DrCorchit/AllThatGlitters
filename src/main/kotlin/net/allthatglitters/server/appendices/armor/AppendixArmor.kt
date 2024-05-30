package net.allthatglitters.server.appendices.armor

import com.drcorchit.justice.utils.StringUtils.normalize
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object AppendixArmor : HtmlFile("Appendix: Armor &amp; Materials", "appendix_armor.html") {
	override val inputDir = File(Generator.inputDir, "misc")
	val armor by lazy {
		File(inputDir, "armor.json")
			.deserialize { Armor.deserialize(it) }
			.associateBy { it.key }
	}

	fun lookupArmor(name: String): Armor {
		return armor[name.normalize()] ?: throw NoSuchElementException("No such armor: $name")
	}

	val armorHeaders = arrayOf(
		"Armor",
		"Block Chance",
		"Cost",
		"Weight",
		"Requirements",
		"Description"
	)

	val materialsFile = File(inputDir, "materials.json")
	val materials = materialsFile.deserialize { Material.deserialize(it) }
	val materialsHeaders = arrayOf(
		"Material",
		"Defense Bonus",
		"Cost Multiplier",
		"Weight Multiplier",
		"Description"
	)

	override fun appendBody(): HtmlFile {
		appendElement("h4", "Armor")
		appendElement(
			"p",
			"Many forms of armor are available for the prospective adventurer. In general, nimble and dexterous characters such as thieves and assassins prefer leather armor or brigandine. Barbarians, mercenaries, inquisitors, and most others usually make use of medium armors, such as breastplate or chainmail. Knights, veterans, and crusaders often wear splinted mail or full plate, being stouter folk."
		)
		val armorTable = HtmlTable().withHeaders(*armorHeaders)
		armor.values.forEach { armorTable.withContent(it.toRow()) }
		append(armorTable)

		appendElement("h4", "Materials")
		appendElement(
			"p",
			"The stats given above are for steel armor. Some armors can be upgraded to rely on tougher metals such as titanium, orichalcum, adamantine, and mithril. These give better results but are significantly more expensive."
		)
		val materialsTable = HtmlTable().withHeaders(*materialsHeaders)
		materials.forEach { materialsTable.withContent(it.toRow()) }
		append(materialsTable)

		return this
	}
}