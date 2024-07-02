package net.allthatglitters.server.concepts

import net.allthatglitters.server.appendices.weapons.Keyword
import net.allthatglitters.server.util.bold

enum class Abbreviation(
	override val abbr: String?,
	override val displayName: String,
	override val description: String
) : Keyword {
	GM(null, "Storyteller", "The person who controls the characters, setting, and story."),
	P(null, "Player", "A person participating in the game."),
	NPC("NPC", "Non-Player Character", "A character controlled by the storyteller."),
	PC("PC", "Player Character", "A character controlled by the player."),

	HP("HP", "Hitpoints", "Used to determine when characters fall unconscious from their wounds."),
	MAX_HP(
		"Max HP",
		"Maximum Hitpoints",
		"The maximum number of hitpoints a character may possess, excluding temporary hitpoints."
	),
	TEMP_HP(
		"Temp HP",
		"Temporary Hitpoints",
		"Additional HP that allow a character to temporarily exceed their maximum HP. Temporary HP is lost before regular HP, and cannot be restored by healing."
	),
	WP("Will", "Willpower", "Expended when using powerful abilities."),
	MAX_WP(
		"Max Will",
		"Maximum Willpower",
		"The maximum amount of willpower a character may possess."
	),
	BC("BC", "Block Chance", "The chance that an attack is mitigated by armor or shields."),
	DC("DC", "Dodge Chance", "The chance that an attack misses a character entirely."),
	MIT("MIT", "Mitigation", "The chance that an attack is mitigated by armor or shields."),
	CT(
		"CT",
		"Critical Threshold",
		"The value on the d20 required for a critical success. Normally equal to 20."
	),
	DT("DT", "Decision Threshold", "A threshold value for a skill check. Meeting this value usually corresponds to success.");
}