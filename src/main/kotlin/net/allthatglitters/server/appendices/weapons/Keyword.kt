package net.allthatglitters.server.appendices.weapons

import net.allthatglitters.server.chapters.combat.CombatChapter
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.Renderable
import net.allthatglitters.server.util.makeTooltip

interface Keyword : Renderable {
	val abbr: String?
	val displayName: String
	val description: String

	override fun render(): String {
		return displayName.bold()
	}

	fun toTooltip(): Renderable {
		return makeTooltip(displayName.lowercase(), description)
	}

	companion object : HasProperties {
		fun parse(str: String): Keyword {
			return WeaponKeyword.parse(str) ?: CombatChapter.lookupDamageType(str)
		}

		override fun getProperty(property: String): Any {
			return AppendixWeapons.lookupModifier(property).toTooltip()
		}
	}
}