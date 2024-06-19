package net.allthatglitters.server.chapters.sheet

import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object Sheet2 : HtmlFile(
	"All That Glitters — Character Sheet",
	"sheet/character_sheet_page_2.html",
	File(generator.inputDir, "sheet")
) {
	override val templatizer = generator.templatizer.extend()
		.withRule("effects") {
			val table = HtmlTable().withClass("inner") as HtmlTable
			val row = table.withDefaultHeaderAttribute("class", "sub").nextRow()

			for (i in 1..2) {
				row.withHeader("Effect", mutableMapOf("width" to "40%"))
					.withHeader("Duration", mutableMapOf("width" to "10%"))
			}
			for (i in 1..7) {
				table.withRow("<br/>", "", "", "")
			}
			HtmlObject("td")
				.withClass("border")
				.withContent(table).render()
		}.withRule("rows") {
			val output = HtmlContent()
			for (i in 1..8) {
				output.withContent(
					HtmlObject("tr")
						.withContent(
							HtmlObject("td")
								.withContent("<br/>")
						)
				)
			}
			output.render()
		}.withRule("abilities") {
			val table = HtmlTable().withClass("inner") as HtmlTable
			val row = table.withDefaultHeaderAttribute("class", "sub").nextRow()

			for (i in 1..3) {
				row.withHeader("Ability", mutableMapOf("width" to "26.4%"))
					.withHeader("Slots", mutableMapOf("width" to "6.6%"))
			}
			for (i in 1..8) {
				table.withRow("<br/>", "", "", "", "", "")
			}
			HtmlObject("td")
				.withClass("border")
				.withContent(table).render()
		}

	override fun appendHeader(): HtmlFile {
		head.withContent(File(inputDir, "header.html").readText())
		return this
	}

	override fun appendBody(): HtmlFile {
		val template = File(inputDir, "template_2.html").readText()
		val body = templatizer.replace(template)
		append(body)
		return this
	}
}