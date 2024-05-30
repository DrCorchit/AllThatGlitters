package net.allthatglitters.server.chapters.classes

import net.allthatglitters.server.util.html.HtmlTable
import net.allthatglitters.server.util.html.Renderable

enum class CombatCategory(
	val description: String,
	classNames: List<String>,
	val bonuses: List<String>
) {
	Warrior(
		"For those who want to play as someone sturdy, wearing heavy armor and fighting with weapons like swords, spears, and halberds.",
		listOf("Barbarian", "Knight", "Mercenary", "Veteran"),
		listOf("Elite Strength", "Martial Arts Proficiency", "Ability: Heavy Attack")
	),
	Renegade(
		"For those focused on stealth, sneak attacks, and subterfuge—or assassination and seduction!",
		listOf("Assassin", "Minstrel", "Thief", "Tinker"),
		listOf("Elite Dexterity", "Stealth Proficiency", "Ability: Precision Strike")
	),
	Mage(
		"For those who wish to wield mystical powers, casting arcane spells and unleashing the elements of frost, fire, and lightning.",
		listOf("Psychic", "Scholar", "Witch", "Wizard"),
		listOf("Elite Intelligence", "Sorcery Proficiency", "Ability: Mana Regeneration")
	),

	Naturalist(
		"For those who want to work closely with nature and animals, living in harmony with the natural world.",
		listOf("Druid", "Marksman", "Ranger", "Warg"),
		listOf("Elite Instinct", "Nature Proficiency", "Ability: Beast Speech")
	),
	Zealot(
		"For those who wish to combine magical and martial prowess in devotion to a higher principle or power.",
		listOf("Crusader", "Inquisitor", "Monk", "Prophet"),
		listOf("Elite Charisma", "Devotion Proficiency", "Ability: Meditation")
	);

	val classes by lazy { classNames.map { name -> CharactersChapter.lookupClass(name) } }

	companion object : Renderable {
		override fun render(): String {
			val output = HtmlTable()
			output.withHeaders("Name", "Description", "Bonuses", "Classes")
			entries.forEach {
				output.withRow(
					it.name,
					it.description,
					it.bonuses.joinToString("") { bonus -> "<p>$bonus</p>" },
					it.classes.joinToString { combatClass -> combatClass.name }
				)
			}
			return output.render()
		}
	}
}