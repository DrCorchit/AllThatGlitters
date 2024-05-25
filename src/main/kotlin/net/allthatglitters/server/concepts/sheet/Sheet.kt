package net.allthatglitters.server.concepts.sheet

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object Sheet : HtmlFile("All That Glitters — Character Sheet", "sheet/character_sheet.html") {
	override val inputDir = File(Generator.inputDir, "sheet")

	override fun appendHeader(): HtmlFile {
		head.withContent(File(inputDir, "header.html").readText())
		return this
	}

	override fun appendBody(): HtmlFile {
		val template = File(inputDir, "template.html").readText()
		val regex = "\\{\\{(.*)}}".toRegex()
		val body = regex.replace(template) { matchResult ->
			File(inputDir, matchResult.groupValues[1]).readText()
		}
		append(body)
		return this
	}
}