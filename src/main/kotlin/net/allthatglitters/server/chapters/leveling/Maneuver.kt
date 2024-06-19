package net.allthatglitters.server.chapters.leveling

import com.google.gson.JsonObject
import net.allthatglitters.server.Generator
import net.allthatglitters.server.concepts.Trainable
import net.allthatglitters.server.concepts.requirement.Requirement

class Maneuver(
	name: String,
	effect: String,
	willpower: Int,
	trainingReqs: List<Requirement>,
	val note: String?
) : Trainable(name, effect, trainingReqs) {

	companion object {
		fun deserialize(info: JsonObject): Maneuver {
			return Generator.generator.deserializer.fromJson(info, Maneuver::class.java)
		}
	}
}