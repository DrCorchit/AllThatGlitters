package net.allthatglitters.server.appendices.abilities

import com.drcorchit.justice.utils.StringUtils.normalize
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object AppendixAbilities : HtmlFile(
	"Appendix: Abilities",
	"appendix_abilities.html",
	File(generator.inputDir, "appendices/2_abilities")
) {
	private val files = listOf("Athletics", "Combat", "Proficiency")
	val abilities = files.flatMap { getTraining(it) }.associateBy { it.name.normalize() }

	fun lookupAbility(str: String): Ability {
		return abilities[str.normalize()] ?: throw NoSuchElementException("No such ability: $str")
	}

	override fun appendBody(): HtmlFile {
		files.forEach { appendAll(it) }
		return this
	}

	private fun appendAll(name: String) {
		appendElement("h4", "$name Training")
		getTraining(name)
			.sortedBy { it.name }
			.forEach { append(it) }
	}

	private fun getTraining(filename: String): List<Ability> {
		return File(inputDir, filename.normalize() + ".json").deserialize { Ability.deserialize(it) }
	}
}