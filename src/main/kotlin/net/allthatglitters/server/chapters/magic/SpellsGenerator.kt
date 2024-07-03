package net.allthatglitters.server.chapters.magic

import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.StringUtils.parseEnum
import com.drcorchit.justice.utils.json.JsonUtils.prettyPrint
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.logging.Logger
import com.google.gson.JsonObject
import net.allthatglitters.server.appendices.magic.College
import net.allthatglitters.server.appendices.magic.Rarity
import net.allthatglitters.server.appendices.magic.Type
import java.io.File

class SpellsGenerator(val inputFile: File) {
	val outputFile = File(inputFile.parentFile, "spells")
	val spells: Map<String, JsonObject>

	init {
		var college: College? = null
		val builder = mutableSetOf<JsonObject>()

		outputFile.mkdir()

		inputFile.readLines().forEach {
			val line = it.replace("\\s+".toRegex(), " ").trim()
			try {
				if (collegeRegex.matches(it)) {
					college = line.parseEnum<College>()
				} else {
					val match = spellsRegex.matchEntire(line)
						?: throw IllegalArgumentException("Unparseable.")
					val spellLevel = match.groupValues[1].toInt()
					val name = match.groupValues[2]
					val infoMatch = spellInfoRegex.matchEntire(match.groupValues[3])!!
					val description = match.groupValues[4]

					val rarity = infoMatch.groupValues[1].trim().parseEnum<Rarity>(Rarity.Common)
					val type = infoMatch.groupValues[2].parseEnum<Type>()
					val castCost = infoMatch.groupValues[3].toInt()
					val concCost =
						infoMatch.groupValues[4].let { cost -> if (cost.isEmpty()) 0 else cost.toInt() }

					val attrReq = spellLevel + 10
					val levelReq = (spellLevel * 2) - 1
					var slots = rarity.slotCost
					if (spellLevel > 2) slots++
					if (spellLevel > 5) slots++

					val trainReqs = JsonObject()
					trainReqs.addProperty("level", levelReq)
					trainReqs.addProperty(college!!.school.primaryAttr.abbr, attrReq)
					trainReqs.addProperty("slots", slots)

					val castReqs = JsonObject()
					castReqs.addProperty("evocation", castCost)
					if (type == Type.Concentration) {
						castReqs.addProperty("concentration", concCost)
					}
					castReqs.addProperty("time", "1 action")

					val output = JsonObject()
					output.addProperty("name", name)
					output.addProperty("level", spellLevel)
					output.addProperty("effect", description)
					output.addProperty("rarity", rarity.name)
					output.addProperty("college", college!!.name)
					output.addProperty("type", type.name)

					output.add("training_reqs", trainReqs)
					output.add("casting_reqs", castReqs)

					//val spell = Spell.deserialize(output)
					builder.add(output)
				}
			} catch (e: Exception) {
				logger.error("Could not parse line of spells file: $line", e)
			}
		}

		spells = builder.associateBy { it.get("name").asString.normalize() }
	}

	fun save() {
		spells.values.groupBy { it.get("college").asString }
			.forEach { (college, spells) ->
				val output = File(outputFile, "${college.lowercase()}.json")
				output.createNewFile()
				val json = spells.toJsonArray().prettyPrint()
				output.writeText(json)
			}
	}

	companion object {
		@JvmStatic
		fun main(vararg args: String) {
			val parser = SpellsGenerator(File("src/main/resources/input/spells.txt"))
			parser.save()
		}

		val logger = Logger.getLogger(SpellsGenerator::class.java)

		val collegeRegex = "\\w+".toRegex()
		val spellsRegex = "\\s*(\\d+)\\|(.+)\\|([a-zA-Z0-9, ]+)\\|(.*)".toRegex()
		val spellInfoRegex = "([a-zA-Z]+ )?([a-zA-Z]+) (\\d+),?(\\d+)?".toRegex()
	}
}