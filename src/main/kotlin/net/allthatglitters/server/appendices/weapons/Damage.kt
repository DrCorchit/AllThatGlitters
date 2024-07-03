package net.allthatglitters.server.appendices.weapons

import com.google.common.collect.ImmutableSet
import net.allthatglitters.server.appendices.weapons.Weapon.Companion.piercing
import net.allthatglitters.server.appendices.weapons.Weapon.Companion.slashing
import net.allthatglitters.server.chapters.combat.CombatChapter
import net.allthatglitters.server.chapters.combat.DamageType
import net.allthatglitters.server.concepts.NumDice
import net.allthatglitters.server.chapters.sheet.Attribute
import net.allthatglitters.server.util.html.Renderable

class Damage(
	//The base damage of the weapon
	val base: Int,
	//The damage dice of the weapon
	val dice: NumDice,
	//The attribute modifiers added to the weapon's damage, if any
	val attr: ImmutableSet<Attribute>,
	//The damage types available with this weapon
	val types: ImmutableSet<DamageType>
): Renderable {

	val isCutAndThrust by lazy {
		types.containsAll(setOf(slashing, piercing))
	}

	override fun render(): String {
		if (base == 0 && dice.count == 0) return "0"

		val output = StringBuilder()
		if (base > 0) output.append("$base + ")
		if (dice.count > 0) output.append("$dice")

		if (attr.isNotEmpty()) {
			output.append(" + ").append(attr.joinToString("/") { it.abbr })
		}

		val types = types.toMutableSet<Keyword>()
		if (isCutAndThrust) {
			types.remove(slashing)
			types.remove(piercing)
			types.add(WeaponKeyword.CutAndThrust)
		}
		types.sortedBy { it.displayName }

		if (types.isNotEmpty()) {
			output.append(" ").append(types.joinToString { it.displayName })
		}

		return output.toString()
	}
}