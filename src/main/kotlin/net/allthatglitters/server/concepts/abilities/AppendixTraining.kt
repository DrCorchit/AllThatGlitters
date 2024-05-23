package net.allthatglitters.server.concepts.abilities

import net.allthatglitters.server.Generator.inputDir
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object AppendixTraining : HtmlFile("Appendix: Training", "appendix_training.html") {
	val trainingDir = File(inputDir, "training")

	override fun appendBody(): HtmlFile {
		listOf("Athletics", "Combat", "Mobility", "Proficiency")
			.forEach { appendAll(it) }
		return this
	}

	private fun appendAll(name: String) {
		appendElement("h4", "$name Training")
		getTraining("${name.lowercase()}.json")
			.sortedBy { it.name }
			.forEach { append(it) }
	}

	private fun getTraining(filename: String): List<Training> {
		return File(trainingDir, filename).deserialize { Training.deserialize(it) }
	}
}