package net.allthatglitters.server.chapters.sheet

import com.drcorchit.justice.utils.StringUtils.normalize
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object Sheet : HtmlFile(
	"All That Glitters — Character Sheet",
	"sheet/character_sheet.html",
	File(generator.inputDir, "sheet")
) {
	override val templatizer = generator.templatizer.extend()
		.withRule("5_skills") {
			val table = HtmlTable().withClass("inner") as HtmlTable
			table.withDefaultHeaderAttribute("class", "sub")
				.nextRow()
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
			val row = table.withDefaultHeaderAttribute("class", "sub")
				.nextRow()
				row.withHeader("Ability", mapOf("style" to "width: 40%"))
			row.withHeader("Slots", mapOf("style" to "width: 10%"))
			row.withHeader("Ability", mapOf("style" to "width: 40%"))
			row.withHeader("Slots", mapOf("style" to "width: 10%"))

			for (i in 1..skills.size) {
				table.withRow("<br/>", "", "", "")
			}
			HtmlObject("td")
				.withClass("border")
				.withContent(table).render()
		}
		.withRule("7_rows") {
			val output = HtmlContent()
			for (i in 1..7) {
				output.withContent(
					HtmlObject("tr")
						.withContent(
							HtmlObject(
								"td",
								mutableMapOf("colspan" to "3")
							).withContent("<br/>")
						)
						.withContent(HtmlObject("td").withContent("<br/>"))
				)
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