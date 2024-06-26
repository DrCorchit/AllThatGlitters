package net.allthatglitters.server.concepts.requirement;

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.allthatglitters.server.chapters.sheet.Character
import net.allthatglitters.server.util.bold

class StringReq(val key: String, val value: String) : Requirement {
	override fun isSatisfied(character: Character): Boolean {
		return true
	}

	override fun render(): String {
		return "${key.bold()}: $value"
	}

	override fun serialize(): Pair<String, JsonElement> {
		return key to JsonPrimitive(value)
	}

	override fun toString(): String {
		return "$key: $value"
	}
}
