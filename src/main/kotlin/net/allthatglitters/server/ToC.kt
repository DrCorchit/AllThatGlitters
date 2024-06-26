package net.allthatglitters.server

import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject

object ToC : HtmlFile("Player's Handbook", "phb_toc.html", generator.inputDir) {

	override fun appendBody(): HtmlFile {
		appendElement(
			"p",
			"This guide is designed for players new to TTRPGs. If you're an experienced player, I suggest skipping the first chapter."
		)
		val list = HtmlObject("ol").withAll(
			generator.chapters.map { chapter ->
				HtmlObject("li").withContent(chapter.linkTo())
					.withContent(
						HtmlObject("ol").withAttribute("type", "i")
							.withAll(chapter.subsections.map { subsection ->
								HtmlObject("li").withContent(subsection.linkTo())
							})
					)
			}
		)

		val appendices = HtmlObject("li").withContent("Appendices")
			.withContent(
				HtmlObject("ol").withAttribute("type", "i")
					.withAll(generator.appendices.map { HtmlObject("li").withContent(it.linkTo()) })
			)
		list.withContent(appendices)
		append(list)
		return this
	}
}