package net.allthatglitters.server.concepts.requirement

import com.drcorchit.justice.utils.math.units.Measurement
import com.drcorchit.justice.utils.math.units.TimeUnits
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.allthatglitters.server.chapters.sheet.Character
import net.allthatglitters.server.util.bold

class TimeReq(override val name: String, val time: Measurement<TimeUnits.Time>) : Requirement {
	override fun isSatisfied(character: Character): Boolean {
		return true
	}

	override fun render(): String {
		return "${name.bold()}: $time"
	}

	override fun toString(): String {
		return time.toString()
	}

	companion object {
		fun parse(name: String, str: String): TimeReq? {
			return try {
				TimeReq(name, TimeUnits.parse(str))
			} catch (e: Exception) {
				//ignore
				null
			}
		}
	}
}