package net.allthatglitters.server.chapters.classes

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.JsonParser
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.Subsection
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object CharactersChapter : HtmlFile("How do I create a character?", "c2.html") {
	override val inputDir: File = File(Generator.inputDir, "chapters/2_characters")
	val races by lazy {
		File(inputDir, "races/races.json")
			.deserialize { Race.deserialize(it) }
			.associateBy { it.name.normalize() }
	}
	val classes by lazy {
		File(inputDir, "classes")
			.listFiles()!!.map {
				try {
					val json = JsonParser.parseString(it.readText())
					CombatClass.deserialize(json.asJsonObject)
				} catch (e: Exception) {
					throw IllegalArgumentException("Error parsing class info for ${it.name}", e)
				}
			}.associateBy { it.name.normalize() }
	}

	fun lookupClass(str: String): CombatClass {
		return classes[str.normalize()] ?: throw NoSuchElementException("No class named $str")
	}

	init {
		addCustomSubsection(RacesSubsection)
		addCustomSubsection(ClassesSubsection)
		addFileSubsection("Pick an Alignment", "alignments")
		addFileSubsection("Write a Backstory", "backstory")
		addFileSubsection("Choose starting Equipment", "equipment")
		addFileSubsection("Fill out your Character Sheet", "sheet")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		append(getOutline())
		subsections.forEach { appendSubsection(it) }
		return this
	}

	private object RacesSubsection : Subsection(this, "Choose a Race", "races") {
		override fun render(): String {
			val output = HtmlContent()
			output.withContent(File(inputDir, "races/intro.html").readText())
			races.values.forEach { output.withContent(it) }
			output.withContent(File(inputDir, "races/outro.html").readText())
			return output.render()
		}
	}

	private object ClassesSubsection : Subsection(this, "Choose a Class", "classes") {
		override fun render(): String {
			val s1 = File(inputDir, "2.1_classes.html").readText()
			val s2 = CombatCategory.render()
			val s3 = File(inputDir, "2.2_classes.html").readText()
			val s4 = classes.values.joinToString("\n") { it.render() }
			return "$s1\n$s2$s3\n$s4"
		}

	}
}