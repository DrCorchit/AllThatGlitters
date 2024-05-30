package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character

class GoldReq(gold: Int) : ValueReq("Gold", gold) {
	override fun getValue(character: Character): Int {
		return character.inventory.gold
	}

	override fun render(): String {
		return toString()
	}

	override fun toString(): String {
		return "$minLevel gold"
	}

	companion object {
		fun parse(str: String): GoldReq? {
			return "(\\d+) gold".toRegex(RegexOption.IGNORE_CASE).matchEntire(str)
				?.let { GoldReq(it.groupValues[1].toInt()) }
		}
	}
}