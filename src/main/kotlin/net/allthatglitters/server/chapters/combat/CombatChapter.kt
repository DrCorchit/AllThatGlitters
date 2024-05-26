package net.allthatglitters.server.chapters.combat

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object CombatChapter : HtmlFile("How does my character fight?", "c5.html") {
	override val inputDir = File(Generator.inputDir, "chapters/5_combat")


}