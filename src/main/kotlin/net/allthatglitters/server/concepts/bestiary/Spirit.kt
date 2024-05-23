package net.allthatglitters.server.concepts.bestiary

import com.google.gson.JsonObject

data class Spirit(
	val latin: String,
	val singular: String,
	val plural: String,
	val description: String
) {

	companion object {
		fun deserialize(obj: JsonObject): Spirit {
			val name = obj.get("singular").asString
			val names = obj.get("plural")?.asString ?: "${name}s"
			return Spirit(obj.get("latin").asString,
				name,
				names,
				obj.get("description").asString)
		}
	}
}