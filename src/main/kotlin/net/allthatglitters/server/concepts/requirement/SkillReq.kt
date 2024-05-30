package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character
import net.allthatglitters.server.chapters.sheet.Proficiency
import net.allthatglitters.server.chapters.sheet.Skill
import net.allthatglitters.server.util.bold

class SkillReq(val skill: Skill, val proficiency: Proficiency) :
	ValueReq(skill.name, proficiency.ordinal) {
	override fun getValue(character: Character): Int {
		return character.skills[skill]!!.ordinal
	}

	override fun render(): String {
		return "${skill.name.bold()}: ${proficiency.adjective}"
	}

	override fun toString(): String {
		return "${skill.name} ${proficiency.adjective}"
	}

	companion object {
		fun parse(str: String): SkillReq? {
			val match =
				"(.*) ${Proficiency.regex}".toRegex(RegexOption.IGNORE_CASE).matchEntire(str)
			if (match != null) {
				val skill = match.groupValues[1].let { Skill.parse(it) }
				val proficiency = match.groupValues[2].let { Proficiency.parse(it) }
				return SkillReq(skill, proficiency)
			} else return null
		}
	}
}