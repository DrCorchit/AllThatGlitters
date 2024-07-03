package net.allthatglitters.server.appendices.magic

enum class Rarity(val slotCost: Int, val description: String) {
	Common(2,
		"Common spells are widely known and easy to learn. Some of these spells are " +
				"simplified versions of more powerful spells studied by academic institutions."
	),
	Scholarly(4,
		"Scholarly spells are studied and taught at a university level." +
				"They are generally inaccessible to the population at large, usually due to the " +
				"high precision and prerequisite knowledge required to cast them correctly."
	),
	Forbidden(3,
		"Forbidden spells are those widely deemed unethical or dangerous. This " +
				"typically includes all forms of necromancy and demonolatry, as well as mind " +
				"control. If you want to learn these spells, you might have to get your hands dirty."
	),
	Esoteric(3,
		"Esoteric spells are not taught by universities, even at a doctoral level, for " +
				"reasons other than legality. Some of these spells have been lost to time, while " +
				"others are studied in secret by druids or cultists. The easiest way to learn " +
				"esoteric spells is via supernatural assistance."
	);
}