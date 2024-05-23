package net.allthatglitters.server.concepts.bestiary

import com.google.gson.JsonObject
import net.allthatglitters.server.Generator

class Plane(val name: String, val description: String) {

	companion object {
		fun deserialize(obj: JsonObject): Plane {
			return Generator.deserializer.fromJson(obj, Plane::class.java)
		}
	}
}