package net.allthatglitters.server.appendices.weapons

import com.google.common.collect.ImmutableSet
import net.allthatglitters.server.concepts.NumDice
import net.allthatglitters.server.chapters.sheet.Attribute

class Damage(
	//The base damage of the weapon
	val base: Int,
	//The damage dice of the weapon
	val dice: NumDice,
	//The attribute modifiers added to the weapon's damage, if any
	val attr: ImmutableSet<Attribute>,
	//The damage types available with this weapon
	val type: ImmutableSet<Type>
) {

	override fun toString(): String {
		if (dice.count == 0) return "0"

		val output = StringBuilder()
		if (base > 0) output.append("$base + ")
		output.append("${dice.count}d${dice.dice.sides}")

		if (attr.isNotEmpty()) {
			output.append(" + ").append(attr.joinToString("/"))
		}

		if (type.size == 2 && type.contains(Type.Slashing) && type.contains(Type.Piercing)) {
			output.append(" <nobr>Cut-and-Thrust</nobr>")
		} else {
			output.append(" ").append(type.joinToString("/") { it.name })
		}

		return output.toString()
	}

	enum class Type : Modifier {
		Piercing, Slashing, Bludgeoning,
		Fire, Cold, Lightning,
		Corrosive, Force, Holy, Profane;

		override val displayName = name
		val description = "Indicates that the weapon deals ${displayName.lowercase()} damage."
		val regex = name.toRegex(RegexOption.IGNORE_CASE)
	}
}