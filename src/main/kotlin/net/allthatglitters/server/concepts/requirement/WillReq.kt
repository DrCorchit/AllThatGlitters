package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character

class WillReq(name: String, minWill: Int): ValueReq(name, minWill) {
	override fun getValue(character: Character): Int {
		return character.wp
	}
}