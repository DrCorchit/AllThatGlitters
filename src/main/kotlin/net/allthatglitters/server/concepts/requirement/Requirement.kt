package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character
import net.allthatglitters.server.concepts.Ability
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.Renderable

interface Requirement : Renderable {
	fun isSatisfied(character: Character): Boolean
}

abstract class ValueReq(val valueName: String, val minLevel: Int) : Requirement {

	abstract fun getValue(character: Character): Int

	override fun isSatisfied(character: Character): Boolean {
		return getValue(character) >= minLevel
	}

	override fun render(): String {
		return "${valueName.bold()}: $minLevel"
	}

	override fun toString(): String {
		return "$valueName: $minLevel"
	}
}

abstract class AbilityReq(val type: String) : Requirement {
	abstract val reqAbilities: Set<Ability>

	override fun isSatisfied(character: Character): Boolean {
		return character.abilities.containsAll(reqAbilities)
	}

	override fun render(): String {
		return "${type.bold()}: ${reqAbilities.joinToString() { it.name }}"
	}

	override fun toString(): String {
		return "$type: ${reqAbilities.joinToString()}"
	}
}

