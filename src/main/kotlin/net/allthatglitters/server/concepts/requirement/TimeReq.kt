package net.allthatglitters.server.concepts.requirement

import com.drcorchit.justice.utils.math.units.Measurement
import com.drcorchit.justice.utils.math.units.TimeUnits
import net.allthatglitters.server.chapters.sheet.Character

class TimeReq(val time: Measurement<TimeUnits.Time>) : Requirement {
	override fun isSatisfied(character: Character): Boolean {
		return true
	}

	override fun render(): String {
		return toString()
	}

	override fun toString(): String {
		return time.toString()
	}

	companion object {
		fun parse(str: String): TimeReq? {
			return try {
				TimeReq(TimeUnits.parse(str))
			} catch (e: Exception) {
				//ignore
				null
			}
		}
	}
}