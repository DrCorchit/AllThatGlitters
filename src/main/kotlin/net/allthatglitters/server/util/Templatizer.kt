package net.allthatglitters.server.util

import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.appendices.armor.AppendixArmor
import net.allthatglitters.server.appendices.items.AppendixItems
import net.allthatglitters.server.appendices.weapons.AppendixWeapons
import net.allthatglitters.server.chapters.sheet.Attribute
import net.allthatglitters.server.chapters.sheet.Skill

val logger = Logger.getLogger(Templatizer::class.java)

class Templatizer private constructor(private val parent: Templatizer? = null) {
	val rules: MutableMap<Regex, (MatchResult) -> String> = mutableMapOf()

	fun withRule(regex: String, rule: (MatchResult) -> String): Templatizer {
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

	private fun attemptMatch(value: String): String {
		return rules.entries.firstNotNullOfOrNull { entry ->
			entry.key.matchEntire(value)?.let { match -> entry.value.invoke(match) }
		} ?: parent?.attemptMatch(value)
		?: throw UnsupportedOperationException("Unable to match \"$value\" to any rule")
	}

	companion object {
		val templateRegex = "\\{\\{(.*?)}}".toRegex()

		fun create(): Templatizer {
			return Templatizer()
		}

		fun getDefault(): Templatizer {
			return create()
				.withRule("(?<parts>\\w+(\\.\\w+)*)(#(?<name>.*))?") {
					val parts = it.groups["parts"]!!.value.split(".")
					val text = it.groups["name"]?.value
					//logger.info("{${it.value}} matched as {parts: $parts name: $text}")
					parsePath(parts, text)
				}
		}

		fun parsePath(parts: List<String>, text: String?): String {
			return when (parts[0]) {
				"version" -> {
					generator.version
				}

				"strings" -> {
					val key = parts[1]
					generator.strings[key.normalize()]!!
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
					val file = generator.lookupFile(parts[1])
					file.getLink(text ?: file.title).render()
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