package net.allthatglitters.server.appendices.weapons

data class Range(val effective: Int, val maximum: Int) {
	override fun toString(): String {
		return "$effective/$maximum"
	}

	companion object {
		val regex = "(\\d+)/(\\d+)".toRegex()
		fun parse(str: String): Range {
			return regex.matchEntire(str)!!.groups.let {
				Range(it[1]!!.value.toInt(), it[2]!!.value.toInt())
			}
		}
	}
}