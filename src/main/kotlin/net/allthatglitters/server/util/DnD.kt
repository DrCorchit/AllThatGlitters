package net.allthatglitters.server.util

import com.drcorchit.justice.utils.logging.Logger
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.File

object DnD {
	private val spellsFile = File("src/main/resources/dnd_spells.json")
	val logger = Logger.getLogger(DnD::class.java)
	val spells = spellsFile.readText().let { JsonParser.parseString(it) }
		.asJsonObject.getAsJsonArray("spell")
		.map { Spell.deserialize(it) }
	val spellsBySchool = spells.groupBy { it.school }

	fun displaySpellsOfLevel(level: Int = 1) {
		val leveledSpells = StringBuilder()
		//show all level N spells (grouped by school) with description
		spellsBySchool.forEach { entry ->
			leveledSpells.append(entry.key.name).append("\n")
			entry.value.filter { it.level == level }.forEach {
				val desc = it.effect.substring(0, it.effect.length.coerceAtMost(150))
				leveledSpells.append(String.format("%40s --- %s\n", it.name, desc))
			}
		}
		logger.info("Level $level spells:\n$leveledSpells")

		leveledSpells.clear()
		//Show all level N spells sorted alphabetically
		spells.filter { it.level == level }
			.sortedBy { s1 -> s1.name }
			.forEach { leveledSpells.append(String.format("%-40s\n", it.name)) }

		logger.info("Level $level spells, alphabetically:\n$leveledSpells")
	}

	fun displaySpellsOfSchool(school: Spell.School = Spell.School.Evocation) {
		val damageSpells = StringBuilder()
		spellsBySchool[school]!!
			.groupBy { it.level }
			.entries.sortedBy { it.key }
			.forEach { entry ->
				damageSpells.append("Level ${entry.key}:\n")
				entry.value.sortedBy { it.name }.forEach { spell ->
					val desc = spell.effect.substring(0, spell.effect.length.coerceAtMost(150))
					damageSpells.append(String.format("%40s --- %s\n", spell.name, desc))
				}
			}

		logger.info("$school spells, by level:\n$damageSpells")
	}

	fun countSpells() {
		val spellCounts = spellsBySchool.mapValues { entry ->
			val sorted = entry.value.groupBy { it.level }
				.mapValues { it.value.size }
				.toSortedMap()
			(0..9).forEach { i -> sorted.computeIfAbsent(i) { 0 } }
			sorted
		}

		val spellCountsStr = StringBuilder()
		spellCountsStr.append("              |  0|  1|  2|  3|  4|  5|  6|  7|  8|  9|Total\n")
		spellCounts.forEach { (school, counts) ->
			var schoolTotal = 0
			spellCountsStr.append(String.format("%14s", school.name))
			counts.forEach { (_, count) ->
				schoolTotal += count
				spellCountsStr.append(String.format("% 4d", count))
			}
			spellCountsStr.append(String.format("%6d\n", schoolTotal))
		}

		//Last row -- totals by level
		spellCountsStr.append("              ")
		spells.groupBy { it.level }
			.values.map { it.size }
			.map { String.format("% 4d", it) }
			.forEach { spellCountsStr.append(it) }
		val total = spells.size
		spellCountsStr.append(String.format("%6d", total))

		logger.info("\n$spellCountsStr")
	}

	class Spell(val name: String, val level: Int, val school: School, val effect: String) {
		enum class School(val code: String) {
			Abjuration("A"),
			Conjuration("C"),
			Divination("D"),
			Enchantment("E"),
			Evocation("V"),
			Illusion("I"),
			Necromancy("N"),
			Transmutation("T");

			companion object {
				fun fromCode(code: String): School {
					return entries.first { it.code.equals(code, true) }
				}
			}
		}

		companion object {
			fun deserialize(ele: JsonElement): Spell {
				try {
					val obj = ele.asJsonObject
					val name = obj.get("name").asString
					val level = obj.get("level").asInt
					val school = obj.get("school").asString.let { School.fromCode(it) }
					val effects = obj.get("entries").asJsonArray.filter { it.isJsonPrimitive }
						.map { it.asString }
					return Spell(name, level, school, effects.joinToString(" "))
				} catch (e: Exception) {
					logger.info(ele.toString())
					throw e
				}
			}
		}
	}

	@JvmStatic
	fun main(vararg args: String) {
		//countSpells()
		//displaySpellsOfLevel(1)
		displaySpellsOfSchool(Spell.School.Evocation)

	}
}