package net.allthatglitters.server.util

import net.allthatglitters.server.util.html.Renderable

interface Tooltip {
	fun getValue(text: String = defaultText): Renderable {
		return makeTooltip(text, value)
	}

	val defaultText: String

	val value: String
}