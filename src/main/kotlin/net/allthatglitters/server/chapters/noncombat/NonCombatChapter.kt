package net.allthatglitters.server.chapters.noncombat

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object NonCombatChapter : HtmlFile("What can my character do besides fighting?", "c6.html") {
	override val inputDir = File(Generator.inputDir, "chapters/6_noncombat")


}