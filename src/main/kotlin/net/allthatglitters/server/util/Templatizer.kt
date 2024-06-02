package net.allthatglitters.server.util

import com.drcorchit.justice.utils.logging.Logger
import net.allthatglitters.server.Generator
import net.allthatglitters.server.appendices.armor.AppendixArmor
import net.allthatglitters.server.appendices.items.AppendixItems
import net.allthatglitters.server.appendices.weapons.AppendixWeapons
import net.allthatglitters.server.chapters.sheet.Attribute
import net.allthatglitters.server.chapters.sheet.Skill
import java.io.File

val logger = Logger.getLogger(Templatizer::class.java)

class Templatizer(val parent: Templatizer? = null) {
	val rules: MutableMap<Regex, (String) -> String> = mutableMapOf()

	fun withRule(regex: String, rule: (String) -> String): Templatizer {
		rules[regex.toRegex()] = rule
		return this
	}

	fun extend(): Templatizer {
		return Templatizer(this)
	}

	fun replace(template: String): String {
		return templateRegex.replace(template) { matchResult ->
			val value = matchResult.groupValues[1].trim()
			replace(attemptMatch(value))
		}
	}

	private fun attemptMatch(str: String): String {
		return rules.entries.firstNotNullOfOrNull {
			if (it.key.matches(str)) {
				//logger.info("  Matched ${it.key} to $str")
				it.value.invoke(str)
			} else null
		} ?: parent?.attemptMatch(str)
		?: throw UnsupportedOperationException("Unable to match $str to any rule")
	}

	companion object {
		val templateRegex = "\\{\\{(.*?)}}".toRegex()

		fun getDefault(): Templatizer {
			val output = Templatizer()
				.withRule("\\w+(\\.\\w+)*#(.*)") {
					val index = it.indexOf('#')
					val parts = it.substring(0, index).split(".")
					val name = it.substring(index+1)
					parsePath(parts, name)
				}
				.withRule("\\w+(\\.\\w+)*") {
					val parts = it.split(".")
					parsePath(parts, null)
				}
				.withRule(".*\\.html") {
					File(Generator.inputDir, it).readText()
				}
			return output
		}

		fun parsePath(parts: List<String>, name: String?): String {
			return when (parts[0]) {
				"version" -> {
					Generator.version
				}
				"attr" -> {
					val attr = Attribute.parse(parts[1])
					attr.name
					//attr.getValue().render()
				}
				"skills" -> {
					val skill = Skill.parse(parts[1])
					skill.getValue().render()
					//skill.name
				}

				"link" -> {
					require(parts.size == 2)
					Generator.lookupFile(parts[1]).getLink().render()
				}

				"armor" -> {
					AppendixArmor.lookupArmor(parts[1]).toTooltip().render()
				}

				"items" -> {
					AppendixItems.lookupItem(parts[1]).toTooltip().render()
				}

				"weapons" -> {
					require(parts.size >= 2)
					when (val key = parts[1]) {
						"modifiers" -> {
							AppendixWeapons.lookupModifier(parts[2])
								.let { makeTooltip(it.first.lowercase(), it.second) }
								.render()
						}

						else -> AppendixWeapons.lookupWeapon(key).toTooltip().render()
					}
				}

				else -> {
					logger.info("Could not match: $parts")
					parts.joinToString(".")
				}
			}
		}
	}
}