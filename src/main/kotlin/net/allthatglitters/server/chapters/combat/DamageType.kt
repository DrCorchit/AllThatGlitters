package net.allthatglitters.server.chapters.combat

import com.google.gson.JsonObject
import net.allthatglitters.server.Generator
import net.allthatglitters.server.appendices.weapons.Keyword
import net.allthatglitters.server.appendices.weapons.WeaponModifier
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.makeTooltip

data class DamageType(
	val name: String,
	val color: String,
	val effect: String,
	val sources: String
) : HasProperties, Keyword {
	val modifier = WeaponModifier(this)
	override val abbr: String? = null
	override val displayName = name
	override val description = "Indicates that the weapon deals ${render()} damage."

	override fun render(): String {
		return HtmlObject("span")
			.withStyle("color:${color}")
			.withContent(name).render()
	}

	fun toBlurb(): String {
		return "<span style=\"font-size: 12pt; color: $color\">${name.bold()}</span><br />$effect<br />Inflicted by $sources"
	}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"name" -> name
			"color" -> color
			"effect" -> effect
			"sources" -> sources
			"tooltip" -> makeTooltip(effect, name)
			else -> null
		}
	}

	companion object : HasProperties {
		fun deserialize(info: JsonObject): DamageType {
			return Generator.generator.deserializer.fromJson(info, DamageType::class.java).copy()
		}

		override fun getProperty(property: String): Any {
			return CombatChapter.lookupDamageType(property)
		}
	}
}