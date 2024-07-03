package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character
import net.allthatglitters.server.util.bold

class WillReq(name: String, minWill: Int): ValueReq(name, minWill) {
	override fun getValue(character: Character): Int {
		return character.wp
	}

	override fun render(): String {
		return "${name.bold()}: $minLevel willpower"
	}
}