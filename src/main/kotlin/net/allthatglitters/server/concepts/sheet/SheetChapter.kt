package net.allthatglitters.server.concepts.sheet

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.Subsection
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object SheetChapter : HtmlFile("What's on my character sheet?", "c3.html") {
	override val inputDir = File(Generator.inputDir, "chapters/3_sheet")
	val attributes by lazy {
		File(inputDir, "attrs.json")
			.deserialize { Attribute.deserialize(it.asJsonObject) }
			.associateBy { it.abbr.uppercase() }
	}
	val skills by lazy {
		File(inputDir, "skills.json")
			.deserialize { Skill.deserialize(it.asJsonObject) }
			.associateBy { it.name.uppercase() }
	}
	val subsections = listOf(
		getSubsection("Character Information", "info", "1"),
		getSubsection("Statistics", "stats", "2"),
		AttributesSubsection,
		getSubsection("Equipment", "equipment", "4"),
		SkillsSubsection,
		getSubsection("Spells &amp; Abilities", "abilities", "6"),
		getSubsection("Backstory &amp; Personality", "backstory", "7"),
		getSubsection("Inventory", "inventory", "8")
	)

	override fun appendBody(): HtmlFile {
		val intro = HtmlObject("p")
			.withContent("The character sheet, available ")
			.withContent(getSheetLink("here"))
			.withContent(", is divided into ${subsections.size} sections:")
		append(intro)
		val list = HtmlObject("ol")
		subsections.forEach { list.withContent(HtmlObject("li").withContent(it.linkTo())) }
		append(list)
		subsections.forEach { appendSubsection(it) }
		return this
	}

	fun getSheetLink(text: String): HtmlObject {
		return HtmlObject("a")
			.withAttribute("href", "character_sheet.html")
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
			attributes.values.forEach {
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
			skills.values.forEach { skill ->
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