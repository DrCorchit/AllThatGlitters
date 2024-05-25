package net.allthatglitters.server.concepts.sheet

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject

data class Skill(val name: String, val description: String, val attrs: ImmutableSet<Attribute>) {

	companion object {
		fun deserialize(obj: JsonObject): Skill {
			return Skill(obj.get("name").asString,
				obj.get("description").asString,
				obj.getAsJsonArray("attrs")
					.map { Attribute.parse(it.asString)!! }
					.let { ImmutableSet.copyOf(it) })
		}

		fun parse(name: String): Skill {
			return SheetChapter.skills[name.uppercase()]!!
		}
	}
}