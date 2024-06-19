package net.allthatglitters.server.util

import com.drcorchit.justice.utils.StringUtils.normalize
import net.allthatglitters.server.appendices.abilities.Ability
import net.allthatglitters.server.appendices.armor.Armor
import net.allthatglitters.server.appendices.items.Item
import net.allthatglitters.server.appendices.magic.Spell
import net.allthatglitters.server.appendices.weapons.Weapon
import net.allthatglitters.server.chapters.combat.DamageType
import net.allthatglitters.server.chapters.combat.StatusEffect
import net.allthatglitters.server.chapters.sheet.Skill
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject

interface HasProperties {
	fun lookup(keys: List<String>, name: String?): String {
		if (keys.isEmpty()) {
			throw IllegalStateException("No key to lookup!")
		}

		val key = keys.first().normalize()
		val value = getProperty(key)
			?: throw IllegalStateException("No property named \"$key\" found in $this")

		return if (keys.size == 1) {
			convertToString(value, name)
		} else if (value is HasProperties) {
			val rest = keys.drop(1)
			value.lookup(rest, name)
		} else {
			throw IllegalArgumentException("value $")
		}
	}

	fun getProperty(property: String): Any?

	companion object {
		fun convertToString(temp: Any, name: String?): String {
			return when (temp) {
				//Tooltip
				is Skill -> temp.toTooltip().render()
				is Weapon -> temp.toTooltip().render()
				is Armor -> temp.toTooltip().render()
				is Item -> temp.toTooltip().render()
				is StatusEffect -> temp.toTooltip().render()
				//Link
				is Ability -> temp.linkTo(name ?: temp.name).render()
				is Spell -> temp.linkTo(name ?: temp.name).render()
				is HtmlFile -> temp.linkTo(name ?: temp.title).render()
				is Subsection -> temp.linkTo(name ?: temp.title).render()
				//Other 
				is DamageType -> HtmlObject("span")
					.withStyle("color:${temp.color}")
					.withContent(temp.name)
					.render()
				else -> temp.toString()
			}
		}
	}
}