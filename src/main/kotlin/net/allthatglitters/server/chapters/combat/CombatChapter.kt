package net.allthatglitters.server.chapters.combat

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object CombatChapter : HtmlFile("How does my character fight?", "c5.html") {
	override val inputDir = File(Generator.inputDir, "chapters/5_combat")

	init {
		addFileSubsection("The Battlefield", "battlefield")
		addFileSubsection("Beginning Combat", "beginning")
		addFileSubsection("Taking Actions", "actions")
		addFileSubsection("Attack and Defense", "attacking")
		addFileSubsection("Moving", "moving")
		addFileSubsection("Grappling", "grappling")
		addFileSubsection("Defeat", "defeat")
		addFileSubsection("Damage Types", "damage_types")
		addFileSubsection("Status Effects", "status_effects")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		append(getOutline())
		subsections.forEach { appendSubsection(it) }
		return this
	}
}