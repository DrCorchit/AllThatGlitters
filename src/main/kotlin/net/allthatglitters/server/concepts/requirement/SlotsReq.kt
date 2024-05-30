package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character

class SlotsReq(slots: Int) : ValueReq("Training Slots", slots) {
	override fun getValue(character: Character): Int {
		return character.slots
	}

	companion object {
		fun parse(str: String): GoldReq? {
			return "(\\d+) slots".toRegex(RegexOption.IGNORE_CASE).matchEntire(str)
				?.let { GoldReq(it.groupValues[1].toInt()) }
		}
	}
}