package net.allthatglitters.server.chapters.sheet

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object SheetChapter : HtmlFile("What's on my character sheet?", "c3.html") {
	override val inputDir = File(Generator.inputDir, "chapters/3_sheet")
	override val templatizer = Generator.templatizer.extend()
		.withRule("attrs") {
			val table = HtmlTable().withHeaders(
				"Attribute Name",
				"What it Measures",
				"Gameplay Effects",
				"Real-Life Equivalence"
			)
			Sheet.attributes.values.forEach {
				table.withRow(
					it.name,
					it.description,
					it.effects.joinToString("<br/>") { effect -> "• $effect" },
					it.interpretation ?: "N/A"
				)
			}
			table.render()
		}
		.withRule("skills") {
			val table = HtmlTable()
				.withHeaders("Skill Name", "Supporting Attributes", "What it Measures")
			Sheet.skills.values.forEach { skill ->
				table.withRow(
					skill.name.bold(),
					skill.attrs.joinToString { it.abbr },
					skill.description
				)
			}
			table.render()
		}

	init {
		addSubsection("Character Information", "info")
		addSubsection("Statistics", "stats")
		addSubsection("Attributes", "attrs")
		addSubsection("Equipment")
		addSubsection("Skills")
		addSubsection("Spells &amp; Abilities", "abilities")
		addSubsection("Backstory &amp; Personality", "backstory")
		addSubsection("Inventory", "inventory")
	}

	override fun appendBody(): HtmlFile {
		val intro = HtmlObject("p")
			.withContent("The character sheet, available ")
			.withContent(getSheetLink("here"))
			.withContent(", is divided into ${subsections.size} sections:")
		append(intro)
		append(getOutline())
		subsections.forEach { appendSubsection(it) }
		return this
	}

	fun getSheetLink(text: String): HtmlObject {
		return HtmlObject("a")
			.withAttribute("href", "sheet/character_sheet.html")
			.withContent(text)
	}
}