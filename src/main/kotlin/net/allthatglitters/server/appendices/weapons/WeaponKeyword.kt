package net.allthatglitters.server.appendices.weapons

import net.allthatglitters.server.concepts.NumDice
import net.allthatglitters.server.util.HasProperties

enum class WeaponKeyword(
	override val displayName: String,
	override val description: String,
	val regex: Regex = displayName.toRegex(RegexOption.IGNORE_CASE)
) : Keyword, HasProperties {
	CutAndThrust(
		"Cut-and-Thrust",
		"The weapon can deal either {{damage.slashing}} or {{damage.piercing}} damage.",
	),
	Nimble(
		"Nimble",
		"Dexterity can be used to provide the weapon's damage, instead of strength."
	),
	Improvised(
		"Improvised",
		"The item is not designed as a weapon. The damage die is rolled with disadvantage."
	),
	Light(
		"Light",
		"The weapon can be used alongside a greatshield, or dual wielded alongside another light weapon."
	),
	TwoHanded(
		"Two-Handed",
		"The weapon must be handled with two hands when attacking."
	),
	Sharp(
		"Sharp",
		"The critical threshold for the weapon is reduced by 1. This ability is lost after critical failing an attack roll, but can be regained by using a whetstone."
	),
	Polearm(
		"Polearm",
		"The weapon can hit targets 10 feet away instead of 5, and is compatible with the <i>Polarm Charge</i> ability."
	),
	Throwable(
		"Throwable",
		"The weapon does not gain the {{weapon_keywords.improvised}} trait when thrown."
	),
	XBow(
		"Crossbow",
		"The weapon is a mechanical crossbow. It requires 30 seconds to reload, and its damage does not benefit from the user's attribute scores."
	),
	Firearm(
		"Firearm",
		"The weapon is a firearm and deals {{damage.force}} damage. It requires 1 minute to reload, and its damage does not benefit from the user's attribute scores."
	),
	Parrying(
		"Parrying",
		"The weapon increases mitigation by the indicated amount when the parry reaction is taken.",
		"Parrying (\\d+)".toRegex()
	) {
		override fun make(match: MatchResult): WeaponModifier {
			val value = match.groupValues[1].toInt()
			return ValueWeaponModifier(this, value)
		}
	},
	Blocking(
		"Blocking",
		"The weapon increases BC by a certain amount, doubled when actively blocking.",
		"Blocking (\\d+)".toRegex()
	) {
		override fun make(match: MatchResult): WeaponModifier {
			val value = match.groupValues[1].toInt()
			return ValueWeaponModifier(this, value)
		}
	},
	Mordhau(
		"Mordhau",
		"The weapon can be held by the blade, using the crossguard as a hammer. This changes the weapon’s damage type to bludgeoning, and replaces the damage die. (Larger swords have this property.)",
		"Mordhau: (\\d+d\\d+)".toRegex()
	) {
		override fun make(match: MatchResult): WeaponModifier {
			val dice = NumDice.parse(match.groupValues[1])
			return ValueWeaponModifier(this, dice)
		}
	},
	Versatile(
		"Versatile",
		"The weapon can be held in an alternate grip for extra damage.",
		"Versatile: (\\d+d\\d+)".toRegex()
	) {
		override fun make(match: MatchResult): WeaponModifier {
			val dice = NumDice.parse(match.groupValues[1])
			return ValueWeaponModifier(this, dice)
		}
	},
	Projectile(
		"Projectile",
		"The weapon fires a specific type of projectile",
		"Projectile: (\\w+)".toRegex()
	) {
		override fun make(match: MatchResult): WeaponModifier {
			val type = match.groupValues[1]
			return ValueWeaponModifier(this, type)
		}
	},
	Ranged(
		"Ranged",
		"The weapon has a range: X/Y. Attacks against targets beyond X feet away have disadvantage; attacks against targets beyond Y feet away automatically fail.",
		"Range: (\\d+)/(\\d+)".toRegex(),
	) {
		override fun make(match: MatchResult): WeaponModifier {
			val r1 = match.groupValues[1].toInt()
			val r2 = match.groupValues[2].toInt()
			return Range(r1, r2)
		}
	};

	override val abbr = null

	open fun make(match: MatchResult): WeaponModifier {
		return WeaponModifier(this)
	}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"name" -> displayName
			"description" -> description
			else -> null
		}
	}

	companion object {
		fun parse(str: String): WeaponKeyword? {
			return entries.firstOrNull { it.displayName.equals(str, true) }
		}
	}
}