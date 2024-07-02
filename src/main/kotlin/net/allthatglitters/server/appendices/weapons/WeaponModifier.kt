package net.allthatglitters.server.appendices.weapons

import net.allthatglitters.server.chapters.combat.CombatChapter
import net.allthatglitters.server.util.html.Renderable

open class WeaponModifier(val keyword: Keyword) : Renderable {

	override fun render(): String {
		return keyword.displayName
	}

	override fun toString(): String {
		return "WeaponModifier($keyword)"
	}

	companion object {
		fun parse(str: String): WeaponModifier {
			val output = WeaponKeyword.entries.firstNotNullOfOrNull { keyword ->
				keyword.regex.matchEntire(str)?.let { keyword.make(it) }
			} ?: CombatChapter.lookupDamageType(str).modifier
			return output
		}
	}
}

open class ValueWeaponModifier(keyword: Keyword, val value: Any): WeaponModifier(keyword) {
	override fun render(): String {
		return "${keyword.displayName}: $value"
	}
}

class Range(val effective: Int, val maximum: Int) : ValueWeaponModifier(WeaponKeyword.Ranged, "$effective/$maximum")