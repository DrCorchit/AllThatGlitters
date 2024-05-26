package net.allthatglitters.server.chapters.magic

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object MagicChapter : HtmlFile("How does my character use magic?", "c7.html") {
	override val inputDir = File(Generator.inputDir, "chapters/7_magic")


}