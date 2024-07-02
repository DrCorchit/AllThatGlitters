package net.allthatglitters.server.chapters.magic

import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import net.allthatglitters.server.appendices.magic.Discipline
import java.io.File
import com.drcorchit.justice.utils.StringUtils.parseEnum
import com.drcorchit.justice.utils.json.JsonUtils.prettyPrint
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import net.allthatglitters.server.appendices.magic.Rarity
import net.allthatglitters.server.appendices.magic.Spell
import net.allthatglitters.server.appendices.magic.Type
import net.allthatglitters.server.concepts.requirement.AttrReq
import net.allthatglitters.server.concepts.requirement.LevelReq
import net.allthatglitters.server.concepts.requirement.WillReq

class SpellsParser(val inputFile: File) {
	val outputFile = File(inputFile.parentFile, "spells")
	val spells: Map<String, Spell>

	init {
		var discipline: Discipline? = null
		val builder = mutableSetOf<Spell>()

		outputFile.mkdir()

		inputFile.readLines().forEach {
			val line = it.replace("\\s+".toRegex(), " ").trim()
			try {
				if (disciplineRegex.matches(it)) {
					discipline = line.parseEnum<Discipline>()
				} else {
					val match = spellsRegex.matchEntire(line)
						?: throw IllegalArgumentException("Unparseable.")
					val spellLevel = match.groupValues[1].toInt()
					val attrReq = AttrReq(discipline!!.school.primaryAttr, spellLevel + 10)
					val levelReq = LevelReq((spellLevel * 2) - 1)

					val name = match.groupValues[2]
					val infoMatch = spellInfoRegex.matchEntire(match.groupValues[3])!!
					val description = match.groupValues[4]

					val rarity = infoMatch.groupValues[1].trim().parseEnum<Rarity>(Rarity.Common)
					val type = infoMatch.groupValues[2].parseEnum<Type>()
					val castCost = infoMatch.groupValues[3].toInt()
					val concCost =
						infoMatch.groupValues[4].let { cost -> if (cost.isEmpty()) 0 else cost.toInt() }
					val trainReqs = listOf(levelReq, attrReq)
					val castReqs = mutableListOf(WillReq("cost", castCost))
					if (type == Type.Concentration) castReqs.add(WillReq("concentration", concCost))

					val output = Spell(
						name,
						rarity,
						discipline!!,
						type,
						null,
						description,
						trainReqs,
						castReqs,
						mapOf()
					)
					builder.add(output)
				}
			} catch (e: Exception) {
				logger.error("Could not parse line of spells file: $line", e)
			}
		}

		spells = builder.associateBy { it.name.normalize() }
	}


	fun save() {
		spells.values.groupBy { it.discipline }
			.forEach { (discipline, spells) ->
				val output = File(outputFile, "${discipline.name.lowercase()}.json")
				output.createNewFile()
				val json = spells.map { it.serialize() }.toJsonArray().prettyPrint()
				output.writeText(json)
			}
	}

	companion object {
		@JvmStatic
		fun main(vararg args: String) {
			val parser = SpellsParser(File("src/main/resources/input/spells.txt"))
			parser.save()
		}

		val logger = Logger.getLogger(SpellsParser::class.java)

		val disciplineRegex = "\\w+".toRegex()
		val spellsRegex = "\\s*(\\d+)\\|(.+)\\|([a-zA-Z0-9, ]+)\\|(.*)".toRegex()
		val spellInfoRegex = "([a-zA-Z]+ )?([a-zA-Z]+) (\\d+),?(\\d+)?".toRegex()
	}
}