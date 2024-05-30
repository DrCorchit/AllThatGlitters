package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character

class OtherReq(val str: String) : Requirement {
	override fun isSatisfied(character: Character): Boolean {
		return true
	}

	override fun render(): String {
		return str
	}

	override fun toString(): String {
		return str
	}
}