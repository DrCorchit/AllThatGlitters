package net.allthatglitters.server.concepts.requirement

import net.allthatglitters.server.chapters.sheet.Character

class LevelReq(minLevel: Int) : ValueReq("Level", minLevel) {
	override fun getValue(character: Character): Int {
		return character.level
	}
}