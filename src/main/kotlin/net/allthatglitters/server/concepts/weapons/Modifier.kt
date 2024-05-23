package net.allthatglitters.server.concepts.weapons

interface Modifier {
	val displayName: String

	companion object {
		fun parse(str: String): Modifier {
			return ValueModifier.parse(str) ?: EnumModifier.parse(str)
			?: Damage.Type.entries.firstOrNull {
				it.regex.matches(str)
			} ?: throw IllegalArgumentException("Unknown modifier: $str")
		}
	}
}