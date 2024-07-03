package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character
import net.allthatglitters.server.concepts.Trainable
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.Renderable

interface Requirement : Renderable {
	val name: String
	fun isSatisfied(character: Character): Boolean
}

abstract class ValueReq(override val name: String, val minLevel: Int) : Requirement {

	abstract fun getValue(character: Character): Int

	override fun isSatisfied(character: Character): Boolean {
		return getValue(character) >= minLevel
	}

	override fun render(): String {
		return "${name.bold()}: $minLevel"
	}

	override fun toString(): String {
		return "$name: $minLevel"
	}
}

abstract class AbilityReq(override val name: String) : Requirement {
	abstract val reqAbilities: Set<Trainable>

	override fun isSatisfied(character: Character): Boolean {
		return character.abilities.containsAll(reqAbilities)
	}

	override fun render(): String {
		return "${name.bold()}: ${reqAbilities.joinToString { it.name }}"
	}

	override fun toString(): String {
		return "$name: ${reqAbilities.joinToString()}"
	}
}

