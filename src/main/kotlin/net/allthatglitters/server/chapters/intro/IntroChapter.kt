package net.allthatglitters.server.chapters.intro

import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.appendices.weapons.Keyword
import net.allthatglitters.server.chapters.sheet.Sheet
import net.allthatglitters.server.concepts.Abbreviation.*
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import java.io.File

object IntroChapter :
	HtmlFile("How do I play the game?", "c1.html", File(generator.inputDir, "chapters/1_intro")) {

	val generalGlossary = listOf(GM, NPC, PC, DT, CT)
	val combatGlossary = listOf(HP, WP, BC, DC, MIT)
	val attributeGlossary = Sheet.attributes.values
	val glossary = mapOf(
		"General" to generalGlossary,
		"Combat" to combatGlossary,
		"Attributes" to attributeGlossary
	)

	override val templatizer = generator.templatizer.extend()
		.withRule("glossary") {
			val output = HtmlContent()
			glossary.entries.forEach {
				output.withContent(HtmlObject("p").withContent(it.key))
				val list = HtmlObject("ul")
					.withAll(
						it.value.map { keyword ->
							HtmlObject("li").withContent(
								getGlossaryEntry(keyword)
							)
						})
				output.withContent(list)
			}
			output.render()
		}

	init {
		//addFileSubsection("Introduction", "intro")
		addSubsection("Session Zero", "session_zero")
		addSubsection("The Game Cycle", "game_cycle")
		addSubsection("Event Rolls", "event_rolls")
		addSubsection("Other Rules", "other_rules")
		addSubsection("Glossary", "glossary")
	}

	fun getGlossaryEntry(keyword: Keyword): String {
		return if (keyword.abbr == null) {
			"${keyword.displayName.bold()}: ${keyword.description}"
		} else {
			"${keyword.abbr!!.bold()}: ${keyword.displayName} (${keyword.description})"
		}
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		subsections.forEach { appendSubsection(it) }
		return this
	}


}