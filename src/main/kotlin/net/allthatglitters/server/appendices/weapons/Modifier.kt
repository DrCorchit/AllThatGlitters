package net.allthatglitters.server.appendices.weapons

import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.Renderable
import net.allthatglitters.server.util.underline

interface Modifier {
	val displayName: String
	val description: String

	fun toTooltip(): Renderable {
		return HtmlObject("span")
			.withAttribute("data-tooltip", description)
			.withContent(displayName.lowercase().underline())
	}

	companion object {
		fun parse(str: String): Modifier {
			return ValueModifier.parse(str) ?: EnumModifier.parse(str)
			?: Damage.Type.entries.firstOrNull {
				it.regex.matches(str)
			} ?: throw IllegalArgumentException("Unknown modifier: $str")
		}
	}
}