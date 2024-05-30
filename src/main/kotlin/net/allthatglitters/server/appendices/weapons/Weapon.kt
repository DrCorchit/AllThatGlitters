package net.allthatglitters.server.appendices.weapons

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonObject
import net.allthatglitters.server.concepts.NumDice
import net.allthatglitters.server.appendices.items.Item
import net.allthatglitters.server.chapters.sheet.Attribute
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable
import net.allthatglitters.server.util.underline

class Weapon(
	val name: String,
	val description: String,
	val damage: Damage,
	val cost: Int,
	val modifiers: ImmutableSet<Modifier>,
	val requirements: ImmutableSet<String>
) : Renderable {

	val item: Item by lazy {
		Item(name, description, cost, 1)
	}

	fun toTooltip() : HtmlObject {
		val tooltip = "$description Damage: $damage"
		return HtmlObject("span")
			.withAttribute("data-tooltip", tooltip)
			.withContent(name.lowercase().underline())
	}

	override fun render(): String {
		val output = HtmlObject("tr")
			.withContent("td", name)
			.withContent("td", damage.toString())
			.withContent("td", cost.toString())
			.withContent("td", modifiers.joinToString { "<nobr>${it.displayName}</nobr>" })
			.withContent("td", requirements.joinToString())
			.withContent("td", description)
		return output.render()
	}

	companion object {
		fun deserialize(obj: JsonObject): Weapon {
			val name = obj.get("name").asString
			val description = obj.get("description").asString
			val dice = obj.get("damage").asString.let { NumDice.parse(it) }
			val cost = obj.get("cost").asInt
			val modifiers = obj.getAsJsonArray("modifiers")
				.map { Modifier.parse(it.asString) }.toMutableSet()
			val requirements = obj.getAsJsonArray("requirements")
				?.map { it.asString }
				?.let { ImmutableSet.copyOf(it) } ?: ImmutableSet.of()

			val damageTypes = modifiers.filterIsInstance<Damage.Type>().toMutableSet()
			if (modifiers.contains(EnumModifier.Firearm)) damageTypes.add(Damage.Type.Force)
			if (modifiers.remove(EnumModifier.CutAndThrust)) {
				damageTypes.add(Damage.Type.Piercing)
				damageTypes.add(Damage.Type.Slashing)
			}
			val attrs = mutableSetOf(Attribute.STR)
			if (modifiers.contains(EnumModifier.Nimble)) attrs.add(Attribute.DEX)
			if (modifiers.contains(EnumModifier.Polearm)) attrs.add(Attribute.SPD)

			return Weapon(
				name,
				description,
				Damage(0, dice, ImmutableSet.copyOf(attrs), ImmutableSet.copyOf(damageTypes)),
				cost,
				ImmutableSet.copyOf(modifiers.filterNot { it is Damage.Type }),
				requirements
			)
		}
	}

}