package net.allthatglitters.server.concepts.bestiary

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.util.italicise

abstract class Category(
	val latinName: String,
	val commonName: String,
	val commonNamePlural: String,
	val description: String
) {
	constructor(obj: JsonObject) : this(
		obj.get("latinName").asString,
		obj.get("commonName").asString,
		obj.get("commonNamePlural")
			?.asString ?: (obj.get("commonName").asString + "s"),
		obj.get("description").asString
	)

	override fun toString(): String {
		return "$commonNamePlural: $latinName"
	}
}

class Phylum(
	obj: JsonObject
) : Category(obj) {
	val genera by lazy { AppendixBestiary.genera.values.filter { it.phylum == this } }
}

class Genus(
	val phylum: Phylum,
	obj: JsonObject
) : Category(obj) {

}

class Species(obj: JsonObject) : Category(obj) {
	val genus = AppendixBestiary.lookupGenus(obj.get("genus").asString)

	val latinFullName = "${genus.phylum.latinName} ${genus.latinName} $latinName".italicise()

	override fun toString(): String {
		return commonName
	}
}