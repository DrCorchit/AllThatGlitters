package net.allthatglitters.server.chapters.intro

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object IntroChapter : HtmlFile("How do I play the game?", "c1.html") {
	override val inputDir = File(Generator.inputDir, "chapters/1_intro")


}