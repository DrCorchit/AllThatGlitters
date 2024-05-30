package net.allthatglitters.server.concepts.requirement;

import net.allthatglitters.server.chapters.sheet.Character
import net.allthatglitters.server.util.bold

class StringReq(val key: String, val value: String) : Requirement {
	override fun isSatisfied(character: Character): Boolean {
		return true
	}

	override fun render(): String {
		return "${key.bold()}: $value"
	}

	override fun toString(): String {
		return "$key: $value"
	}
}
