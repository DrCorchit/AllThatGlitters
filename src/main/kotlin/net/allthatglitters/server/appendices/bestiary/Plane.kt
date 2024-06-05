package net.allthatglitters.server.appendices.bestiary

import com.google.gson.JsonObject
import net.allthatglitters.server.Generator.Companion.generator

class Plane(val name: String, val description: String) {

	companion object {
		fun deserialize(obj: JsonObject): Plane {
			return generator.deserializer.fromJson(obj, Plane::class.java)
		}
	}
}