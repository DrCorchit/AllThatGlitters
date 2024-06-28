package net.allthatglitters.server.concepts.requirement

import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.allthatglitters.server.chapters.sheet.Character
import net.allthatglitters.server.concepts.Trainable
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.Renderable

interface Requirement : Renderable {
	fun isSatisfied(character: Character): Boolean

	fun serialize(): Pair<String, JsonElement>
}

abstract class ValueReq(val valueName: String, val minLevel: Int) : Requirement {

	abstract fun getValue(character: Character): Int

	override fun isSatisfied(character: Character): Boolean {
		return getValue(character) >= minLevel
	}

	override fun render(): String {
		return "${valueName.bold()}: $minLevel"
	}

	override fun serialize(): Pair<String, JsonElement> {
		return valueName to JsonPrimitive(minLevel)
	}

	override fun toString(): String {
		return "$valueName: $minLevel"
	}
}

abstract class AbilityReq(val type: String) : Requirement {
	abstract val reqAbilities: Set<Trainable>

	override fun isSatisfied(character: Character): Boolean {
		return character.abilities.containsAll(reqAbilities)
	}

	override fun render(): String {
		return "${type.bold()}: ${reqAbilities.joinToString() { it.name }}"
	}

	override fun serialize(): Pair<String, JsonElement> {
		return "spells" to reqAbilities.map { JsonPrimitive(it.name) }.toJsonArray()
	}

	override fun toString(): String {
		return "$type: ${reqAbilities.joinToString()}"
	}
}

