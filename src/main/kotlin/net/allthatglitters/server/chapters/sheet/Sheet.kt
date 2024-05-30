package net.allthatglitters.server.chapters.sheet

import com.drcorchit.justice.utils.StringUtils.normalize
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.Templatizer
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object Sheet : HtmlFile("All That Glitters — Character Sheet", "sheet/character_sheet.html") {
	override val inputDir = File(Generator.inputDir, "sheet")
	override val templatizer = Generator.templatizer.extend()
		.withRule("5_skills") {
			val table = HtmlTable().withClass("inner") as HtmlTable
				table.nextRow()
				.withDefaultHeaderAttribute("class", "sub")
				.withHeader("Skill")
				.withHeader("Base")
				.withHeader("Bonuses")
				.withHeader("Total")
				.table()
			skills.values.map { table.withContent(it.toRow()) }
			HtmlObject("td")
				.withClass("border")
				.withAttribute("colspan", "3")
				.withContent(table).render()
		}
		.withRule("6_abilities") {
			val table = HtmlTable().withClass("inner")
				.withStyle("background: white url(/images/books.png) no-repeat bottom; background-size: 100%") as HtmlTable
			table.nextRow().withDefaultHeaderAttribute("class", "sub")
				.withHeader("Ability", mutableMapOf("class" to "sub", "width" to "40%"))
				.withHeader("Slots", mutableMapOf("class" to "sub", "width" to "10%"))
				.withHeader("Ability", mutableMapOf("class" to "sub", "width" to "40%"))
				.withHeader("Slots", mutableMapOf("class" to "sub", "width" to "10%"))

			table.withHeaders()
			for (i in 1..15) {
				table.withRow("<br/>", "", "", "")
				//table.withRow("${2 * i - 1}.", "<br/>", "${2*i}.", "<br />")
			}
			HtmlObject("td")
				.withClass("border")
				.withContent(table).render()
		}
		.withRule("7_rows") {
			val output = HtmlContent()
			for (i in 1..8) {
				output.withContent(HtmlObject("tr")
					.withContent(HtmlObject("td", mutableMapOf("colspan" to "3")).withContent("<br/>"))
					.withContent(HtmlObject("td").withContent("<br/>")))
			}
			output.render()
		}

	val attributes by lazy {
		File(inputDir, "attrs.json")
			.deserialize { Attribute.deserialize(it.asJsonObject) }
			.associateBy { it.abbr.normalize() }
	}

	val skills by lazy {
		File(inputDir, "skills.json")
			.deserialize { Skill.deserialize(it.asJsonObject) }
			.associateBy { it.name.normalize() }
	}

	override fun appendHeader(): HtmlFile {
		head.withContent(File(inputDir, "header.html").readText())
		return this
	}

	override fun appendBody(): HtmlFile {
		val template = File(inputDir, "template.html").readText()
		val body = templatizer.replace(template)
		append(body)
		return this
	}
}