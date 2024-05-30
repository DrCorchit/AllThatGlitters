package net.allthatglitters.server.chapters.sheet

import net.allthatglitters.server.appendices.armor.Armor
import net.allthatglitters.server.appendices.weapons.Weapon
import net.allthatglitters.server.chapters.classes.CombatClass
import net.allthatglitters.server.chapters.classes.Race
import net.allthatglitters.server.concepts.Ability

class Character(
	val race: Race,
	val combatClass: CombatClass,
	val level: Int,
	val hp: Int,
	val wp: Int,
	val slots: Int,
	val mainWeapon: Weapon?,
	val sideWeapon: Weapon?,
	val armor: Armor?,
	val inventory: Inventory,
	val attrs: Map<Attribute, Int>,
	val skills: Map<Skill, Proficiency>,
	val abilities: List<Ability>
) {

	val maxHP get() = attrs[Attribute.STR]!! * (level + 1) / 2
	val maxWP get() = (attrs[Attribute.CHA]!! + level) * 2

}