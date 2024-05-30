package net.allthatglitters.server.chapters.sheet

import com.drcorchit.justice.utils.StringUtils.normalize

enum class Proficiency(val adjective: String) {
	Deficient("Deficiency"),
	None("Apprenticeship"),
	Proficient("Proficiency"),
	Expert("Expertise"),
	Master("Mastery");

	companion object {
		val regex = entries.joinToString("|", "(", ")") { it.name.lowercase() }

		val proficiencies = entries.associateBy { it.name.normalize() }

		fun parse(str: String): Proficiency {
			return proficiencies[str.normalize()] ?: throw NoSuchElementException("No such proficiency: $str")
		}
	}
}