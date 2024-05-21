package net.allthatglitters.server.util

import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject

object Navigation {
	fun render(current: Int, max: Int): String {
		val prev = if (current > 1) {
			"c${current - 1}.html" to "Retreat to Chapter ${current - 1}"
		} else null
		val next = if (current < max) {
			"c${current + 1}.html" to "Advance to Chapter ${current + 1}"
		} else null

		return render(prev, next)
	}

	fun render(prev: HtmlFile?, next: HtmlFile?): String {
		return render(prev?.let { prev.fileName to prev.title },
			next?.let { next.fileName to next.title })
	}

	fun render(prev: Pair<String, String>?, next: Pair<String, String>?): String {
		val output = HtmlContent()
		output.withContent(HorizontalRule)
		val div = HtmlObject("div")
			.withClass("row")
			.withStyle("justify-content:space-evenly; margin: 0")

		if (prev != null) {
			div.withContent(
				HtmlObject("div")
					.withClass("column-shrink")
					.withContent(
						HtmlObject("a")
							.withAttribute("href", prev.first)
							.withContent(prev.second)
					)
			)
		}

		div.withContent(
			HtmlObject("div")
				.withClass("column-shrink")
				.withContent(
					HtmlObject("a")
						.withAttribute("href", "phb_toc.html")
						.withContent("Back to the Table of Contents")
				)
		)

		if (next != null) {
			div.withContent(
				HtmlObject("div")
					.withClass("column-shrink")
					.withContent(
						HtmlObject("a")
							.withAttribute("href", next.first)
							.withContent(next.second)
					)
			)
		}
		output.withContent(div)
		output.withContent(HorizontalRule)
		return output.render()
	}
}