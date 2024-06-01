package net.allthatglitters.server.chapters.intro

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object IntroChapter : HtmlFile("How do I play the game?", "c1.html") {
	override val inputDir = File(Generator.inputDir, "chapters/1_intro")

	init {
		//addFileSubsection("Introduction", "intro")
		addFileSubsection("Session Zero", "session_zero")
		addFileSubsection("The Game Cycle", "game_cycle")
		addFileSubsection("Event Rolls", "event_rolls")
		addFileSubsection("Other Rules", "other_rules")
		addFileSubsection("Abbreviations", "abbreviations")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		subsections.forEach { appendSubsection(it) }
		return this
	}

}