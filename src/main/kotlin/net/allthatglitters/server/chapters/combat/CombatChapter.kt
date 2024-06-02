package net.allthatglitters.server.chapters.combat

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object CombatChapter : HtmlFile("How does my character fight?", "c5.html") {
	override val inputDir = File(Generator.inputDir, "chapters/5_combat")

	init {
		addSubsection("The Battlefield", "battlefield")
		addSubsection("Beginning Combat", "beginning")
		addSubsection("Taking Actions", "actions")
		addSubsection("Attack and Defense", "attacking")
		addSubsection("Moving", "moving")
		addSubsection("Grappling", "grappling")
		addSubsection("Defeat", "defeat")
		addSubsection("Damage Types", "damage_types")
		addSubsection("Status Effects", "status_effects")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		append(getOutline())
		subsections.forEach { appendSubsection(it) }
		return this
	}
}