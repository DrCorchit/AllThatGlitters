package net.allthatglitters.server.chapters.leveling

import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object LevelingChapter : HtmlFile("How does my character become stronger?", "c4.html", File(generator.inputDir, "chapters/4_leveling")) {

	init {
		addSubsection("Leveling", "leveling")
		addSubsection("Training", "training")
		addSubsection("Looting", "looting")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		subsections.forEach { appendSubsection(it) }
		return this
	}
}