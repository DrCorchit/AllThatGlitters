package net.allthatglitters.server.chapters.sheet

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.Subsection
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object SheetChapter : HtmlFile("What's on my character sheet?", "c3.html") {
	override val inputDir = File(Generator.inputDir, "chapters/3_sheet")

	init {
		addFileSubsection("Character Information", "info")
		addFileSubsection("Statistics", "stats")
		addCustomSubsection(AttributesSubsection)
		addFileSubsection("Equipment", "equipment")
		addCustomSubsection(SkillsSubsection)
		addFileSubsection("Spells &amp; Abilities", "abilities")
		addFileSubsection("Backstory &amp; Personality", "backstory")
		addFileSubsection("Inventory", "inventory")
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

	private object AttributesSubsection : Subsection(this, "Attributes", "attributes") {
		override fun render(): String {
			val s1 = File(inputDir, "3.1_attrs.html").readText()
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
			val s2 = table.render()

			val s3 = File(inputDir, "3.2_attrs.html").readText()
			return "$s1\n$s2\n$s3"
		}
	}

	private object SkillsSubsection : Subsection(this, "Skills", "skills") {
		override fun render(): String {
			val s1 = File(inputDir, "5.1_skills.html").readText()
			val table = HtmlTable()
				.withHeaders("Skill Name", "Supporting Attributes", "What it Measures")
			Sheet.skills.values.forEach { skill ->
				table.withRow(
					skill.name.bold(),
					skill.attrs.joinToString { it.abbr },
					skill.description
				)
			}
			val s2 = table.render()
			val s3 = File(inputDir, "5.2_skills.html").readText()
			return "$s1\n$s2\n$s3"
		}

	}
}