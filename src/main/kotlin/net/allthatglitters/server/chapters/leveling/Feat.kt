package net.allthatglitters.server.chapters.leveling

import com.google.gson.JsonObject
import net.allthatglitters.server.Generator
import net.allthatglitters.server.concepts.Trainable
import net.allthatglitters.server.concepts.requirement.Requirement

class Feat(name: String, effect: String, trainingReqs: List<Requirement>, val note: String?) :
	Trainable(name, effect, trainingReqs) {

	companion object {
		fun deserialize(info: JsonObject): Feat {
			return Generator.generator.deserializer.fromJson(info, Feat::class.java)
		}


 	}
}