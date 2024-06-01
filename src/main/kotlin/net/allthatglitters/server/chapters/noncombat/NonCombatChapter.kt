package net.allthatglitters.server.chapters.noncombat

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object NonCombatChapter : HtmlFile("What can my character do besides fighting?", "c6.html") {
	override val inputDir = File(Generator.inputDir, "chapters/6_noncombat")

	init {
		addFileSubsection("Resting")
		addFileSubsection("Working")
		addFileSubsection("Traveling")
		addFileSubsection("Swimming")
		addFileSubsection("Falling")
		addFileSubsection("Sneaking")
		addFileSubsection("Pickpocketing")
		addFileSubsection("Lockpicking")
		addFileSubsection("Trading &amp; Haggling", "trading")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		append(getOutline())
		subsections.forEach { appendSubsection(it) }
		return this
	}

}