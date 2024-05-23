package net.allthatglitters.server.concepts.bestiary

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject

abstract class Category(
	val latin: String,
	val singular: String,
	val plural: String,
	val adjective: String,
	val description: String
) {
	constructor(obj: JsonObject) : this(
		obj.get("latin").asString,
		obj.get("singular").asString,
		obj.get("plural")?.asString ?: (obj.get("singular").asString + "s"),
		obj.get("adjectuve")?.asString ?: obj.get("singular").asString,
		obj.get("description").asString
	)

	override fun toString(): String {
		return "$plural: $latin"
	}
}

class Phylum(
	obj: JsonObject
) : Category(obj) {
	val genera = obj.getAsJsonArray("genera")
		?.map { Genus(this, it.asJsonObject) }
		?.let { ImmutableSet.copyOf(it) }
		?: ImmutableSet.of()
}

class Genus(
	val phylum: Phylum,
	obj: JsonObject
) : Category(obj) {
	/*
	val genera = obj.getAsJsonArray("phyla")
		?.map { Genus(it.asJsonObject) }
		?.let { ImmutableSet.copyOf(it) }
		?: ImmutableSet.of()
	 */
}