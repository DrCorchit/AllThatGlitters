package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Attribute
import net.allthatglitters.server.chapters.sheet.Character

class AttrReq(val attribute: Attribute, minLevel: Int) : ValueReq(attribute.displayName, minLevel) {
	override fun getValue(character: Character): Int {
		return character.attrs[attribute]!!
	}

	companion object {
		fun parse(str: String): AttrReq? {
			val match =
				"${Attribute.regex} (\\d+)".toRegex(RegexOption.IGNORE_CASE).matchEntire(str)
			if (match != null) {
				val attr = match.groupValues[1].let { Attribute.parse(it) }
				val num = match.groupValues[2].toInt()
				return AttrReq(attr, num)
			}
			return null
		}
	}
}