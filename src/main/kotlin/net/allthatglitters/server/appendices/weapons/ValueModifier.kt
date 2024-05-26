package net.allthatglitters.server.appendices.weapons

import net.allthatglitters.server.concepts.NumDice

class ValueModifier<T>(type: Type, val value: T) : Modifier {
	override val displayName = "${type.displayName}: $value"

	enum class Type(
		val displayName: String,
		val description: String,
		val regex: Regex,
	) {
		Parrying(
			"Parrying",
			"The weapon increases mitigation by the indicated amount when the parry reaction is taken.",
			parryingRegex
		) {
			override fun make(str: String): Any {
				return str.toInt()
			}
		},
		Blocking(
			"Blocking",
			"The weapon increases BC by a certain amount, doubled when actively blocking.",
			blockingRegex
		) {
			override fun make(str: String): Any {
				return str.toInt()
			}
		},
		Mordhau(
			"Mordhau",
			"The weapon can be held by the blade, using the crossguard as a hammer. This changes the weapon’s damage type to bludgeoning, and replaces the damage die. (Larger swords have this property.)",
			mordhauRegex
		) {
			override fun make(str: String): Any {
				return NumDice.parse(str)
			}
		},
		Versatile(
			"Versatile",
			"The weapon can be held in an alternate grip for extra damage.",
			versatileRegex
		) {
			override fun make(str: String): Any {
				return NumDice.parse(str)
			}
		},

		Projectile(
			"Projectile",
			"The weapon fires a specific type of projectile",
			projectileRegex
		) {
			override fun make(str: String): Any {
				return str
			}
		},
		Ranged(
			"Ranged",
			"The weapon has a range: X/Y. Attacks against targets beyond X feet away have disadvantage; attacks against targets beyond Y feet away automatically fail.",
			rangedRegex,
		) {
			override fun make(str: String): Any {
				return Range.parse(str)
			}
		};

		abstract fun make(str: String): Any
	}

	companion object {
		val parryingRegex = "Parrying (\\d+)".toRegex()
		val blockingRegex = "Blocking (\\d+)".toRegex()
		val mordhauRegex = "Mordhau: (\\d+d\\d+)".toRegex()
		val versatileRegex = "Versatile: (\\d+d\\d+)".toRegex()
		val projectileRegex = "Projectile: (\\w+)".toRegex()
		val rangedRegex = "Range: (\\d+/\\d+)".toRegex()

		fun parse(str: String): ValueModifier<Any>? {
			return Type.entries.firstOrNull { it.regex.matches(str) }?.let {
				ValueModifier(it, it.make(it.regex.matchEntire(str)!!.groups[1]!!.value))
			}
		}
	}
}