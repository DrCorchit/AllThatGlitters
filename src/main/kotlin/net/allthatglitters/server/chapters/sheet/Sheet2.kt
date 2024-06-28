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
	//total should add to 31
	val effectRows = 5
	val abilityRows = 8
	val inventoryRows = 10
	val backstoryRows = 8

	override val templatizer = generator.templatizer.extend()
		.withRule("effects") {
			val table = HtmlTable().withClass("inner") as HtmlTable
			val row = table.withDefaultHeaderAttribute("class", "sub").nextRow()

			for (i in 1..2) {
				row.withHeader("Effect", mutableMapOf("width" to "40%"))
					.withHeader("Duration", mutableMapOf("width" to "10%"))
			}
			for (i in 1..effectRows) {
				table.withRow("<br/>", "", "", "")
			}
			HtmlObject("td")
				.withClass("border")
				.withContent(table).render()
		}.withRule("abilities") {
			val table = HtmlTable().withClass("inner") as HtmlTable
			val row = table.withDefaultHeaderAttribute("class", "sub").nextRow()

			for (i in 1..3) {
				row.withHeader("Ability", mutableMapOf("width" to "26.4%"))
					.withHeader("Slots", mutableMapOf("width" to "6.6%"))
			}
			for (i in 1..abilityRows) {
				table.withRow("<br/>", "", "", "", "", "")
			}
			HtmlObject("td")
				.withClass("border")
				.withContent(table).render()
		}.withRule("inventory") {
			withRows(inventoryRows).render()
		}.withRule("backstory") {
			withRows(backstoryRows).render()
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

	private fun withRows(num: Int): HtmlContent {
		val output = HtmlContent()
		for (i in 1..num) {
			output.withContent(
				HtmlObject("tr")
					.withContent(
						HtmlObject("td")
							.withContent("<br/>")
					)
			)
		}
		return output
	}
}