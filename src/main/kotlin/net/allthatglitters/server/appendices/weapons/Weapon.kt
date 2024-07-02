package net.allthatglitters.server.appendices.weapons

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.concepts.NumDice
import net.allthatglitters.server.appendices.items.Item
import net.allthatglitters.server.chapters.combat.CombatChapter
import net.allthatglitters.server.chapters.combat.DamageType
import net.allthatglitters.server.chapters.sheet.Attribute
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable
import net.allthatglitters.server.util.makeTooltip

class Weapon(
	val name: String,
	val description: String,
	val damage: Damage,
	val cost: Int,
	val modifiers: ImmutableSet<WeaponModifier>,
	val requirements: ImmutableSet<String>
) : Renderable {

	val item: Item by lazy {
		Item(name, description, cost, 1)
	}

	fun toTooltip(): Renderable {
		return makeTooltip(name.lowercase(), "$description<br />Damage: $damage")
	}

	override fun render(): String {
		val output = HtmlObject("tr")
			.withContent("td", name)
			.withContent("td", damage.toString())
			.withContent("td", cost.toString())
			.withContent("td", modifiers.joinToString { "<nobr>${it.render()}</nobr>" })
			.withContent("td", requirements.joinToString())
			.withContent("td", description)
		return output.render()
	}

	companion object : HasProperties {

		val force = CombatChapter.lookupDamageType("force")
		val slashing = CombatChapter.lookupDamageType("slashing")
		val piercing = CombatChapter.lookupDamageType("piercing")

		fun deserialize(obj: JsonObject): Weapon {
			val name = obj.get("name").asString
			val description = obj.get("description").asString
			val dice = obj.get("damage").asString.let { NumDice.parse(it) }
			val cost = obj.get("cost").asInt
			val modifiers: MutableMap<Keyword, WeaponModifier> = obj.getAsJsonArray("modifiers")
				.map { WeaponModifier.parse(it.asString) }
				.associateBy { it.keyword }
				.toMutableMap()
			val requirements = obj.getAsJsonArray("requirements")
				?.map { it.asString }
				?.let { ImmutableSet.copyOf(it) } ?: ImmutableSet.of()

			val damageTypes = modifiers.keys.filterIsInstance<DamageType>().toMutableSet()

			if (modifiers.contains(WeaponKeyword.Firearm)) {
				damageTypes.add(force)
			}

			if (modifiers.remove(WeaponKeyword.CutAndThrust) != null) {
				damageTypes.add(slashing)
				damageTypes.add(piercing)
			}

			val attrs = mutableSetOf(Attribute.STR)
			if (modifiers.contains(WeaponKeyword.Nimble)) attrs.add(Attribute.DEX)
			if (modifiers.contains(WeaponKeyword.Polearm)) attrs.add(Attribute.SPD)

			return Weapon(
				name,
				description,
				Damage(0, dice, ImmutableSet.copyOf(attrs), ImmutableSet.copyOf(damageTypes)),
				cost,
				ImmutableSet.copyOf(modifiers.values.filterNot { it.keyword is DamageType }),
				requirements
			)
		}

		override fun getProperty(property: String): Any {
			return AppendixWeapons.lookupWeapon(property)
		}
	}
}