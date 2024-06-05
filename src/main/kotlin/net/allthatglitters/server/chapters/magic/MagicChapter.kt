package net.allthatglitters.server.chapters.magic

import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object MagicChapter : HtmlFile("How does my character use magic?", "c7.html", File(generator.inputDir, "chapters/7_magic")) {

	init {
		addSubsection("Learning Spells")
		addSubsection("Casting Spells")
		addSubsection("Schools of Sorcery")
	}

	override fun appendBody(): HtmlFile {
		//append(File(inputDir, "0_intro.html").readText())
		subsections.forEach { appendSubsection(it) }
		return this
	}
}