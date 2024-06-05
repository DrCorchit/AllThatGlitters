package net.allthatglitters.server.chapters.noncombat

import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object NonCombatChapter : HtmlFile(
	"What can my character do besides fighting?",
	"c6.html",
	File(generator.inputDir, "chapters/6_noncombat")
) {
	init {
		addSubsection("Resting")
		addSubsection("Working")
		addSubsection("Traveling")
		addSubsection("Swimming")
		addSubsection("Falling")
		addSubsection("Sneaking")
		addSubsection("Pickpocketing")
		addSubsection("Lockpicking")
		addSubsection("Trading &amp; Haggling", "trading")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		append(getOutline())
		subsections.forEach { appendSubsection(it) }
		return this
	}

}