package net.allthatglitters.server.appendices.abilities

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object AppendixFeats : HtmlFile("Appendix: Training", "appendix_training.html") {
	override val inputDir = File(Generator.inputDir, "appendices/2_feats")

	override fun appendBody(): HtmlFile {
		listOf("Athletics", "Combat", "Proficiency").forEach { appendAll(it) }
		return this
	}

	private fun appendAll(name: String) {
		appendElement("h4", "$name Training")
		getTraining("${name.lowercase()}.json")
			.sortedBy { it.name }
			.forEach { append(it) }
	}

	private fun getTraining(filename: String): List<Training> {
		return File(inputDir, filename).deserialize { Training.deserialize(it) }
	}
}