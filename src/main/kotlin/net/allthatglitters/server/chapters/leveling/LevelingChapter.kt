package net.allthatglitters.server.chapters.leveling

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object LevelingChapter : HtmlFile("How does my character become stronger?", "c4.html") {
	override val inputDir = File(Generator.inputDir, "chapters/4_leveling")


}