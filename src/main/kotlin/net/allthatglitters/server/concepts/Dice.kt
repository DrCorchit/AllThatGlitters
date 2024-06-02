package net.allthatglitters.server.concepts

import net.allthatglitters.server.concepts.Dice.D20

data class NumDice(val count: Int, val dice: Dice) {
	override fun toString(): String {
		return if (count == 0) "0" else "${count}d${dice.sides}"
	}

	companion object {
		fun parse(str: String): NumDice {
			if (str == "0") return NumDice(0, D20)
			return "(\\d+)d(\\d+)".toRegex().matchEntire(str)?.let {
				val dieCount = it.groups[1]!!.value.toInt()
				val faceCount = it.groups[2]!!.value.toInt()
				return NumDice(dieCount, Dice.byValue(faceCount))
			} ?: throw IllegalArgumentException("Unable to parse dice: $str")
		}
	}
}

enum class Dice(val sides: Int) {
	D4(4),
	D6(6),
	D8(8),
	D10(10),
	D12(12),
	D20(20),
	D100(100);

	companion object {
		fun byValue(sides: Int): Dice {
			return entries.firstOrNull { it.sides == sides }
				?: throw IllegalArgumentException("Unsupported die face count: $sides")
		}
	}
}