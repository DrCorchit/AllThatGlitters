package net.allthatglitters.server.chapters.combat

import com.google.gson.JsonObject
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.html.Renderable
import net.allthatglitters.server.util.makeTooltip

data class StatusEffect(
	val name: String,
	val effect: String,
	val causes: String,
	val recovery: String,
	val notes: String
): HasProperties {

	fun toTooltip(): Renderable {
		return makeTooltip(name, effect)
	}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"name" -> name
			"effect" -> effect
			"causes" -> causes
			"recovery" -> recovery
			"notes" -> notes
			else -> null
		}
	}

	override fun toString(): String {
		return name
	}

	companion object : HasProperties {
		fun deserialize(info: JsonObject): StatusEffect {
			return Generator.generator.deserializer.fromJson(info, StatusEffect::class.java)
		}

		override fun getProperty(property: String): Any {
			return CombatChapter.lookupStatusEffect(property)
		}
	}
}