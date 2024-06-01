package net.allthatglitters.server.chapters.classes

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.JsonParser
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.Subsection
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
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
		addFileSubsection("Alignments", "alignments")
		addFileSubsection("Writing a Backstory", "backstory")
		addFileSubsection("Starting Equipment", "equipment")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		//Don't do this because we want to show the subsection names differently at the top of the chapter
		//append(getOutline())
		val list = HtmlObject("ol")
		val altNames = listOf(
			"Choose A Race",
			"Choose a Class",
			"Pick an Alignment",
			"Write a Backstory",
			"Choose starting Equipment",
		)
		//TODO warn if altNames length != subsections length
		subsections.zip(altNames).forEach {
			val section = it.first
			val title = it.second
			list.withContent(HtmlObject("li").withContent(section.linkTo(title)))
		}
		append(list)
		subsections.forEach { appendSubsection(it) }
		appendElement(
			"p",
			"Once you've completed steps 1-5, you're now ready to fill out your character sheet. This is covered in the next chapter."
		)
		return this
	}

	private object RacesSubsection : Subsection(this, "Races", "races") {
		override fun render(): String {
			val output = HtmlContent()
			output.withContent(File(inputDir, "races/intro.html").readText())
			races.values.forEach { output.withContent(it) }
			output.withContent(File(inputDir, "races/outro.html").readText())
			return output.render()
		}
	}

	private object ClassesSubsection : Subsection(this, "Classes", "classes") {
		override fun render(): String {
			val s1 = File(inputDir, "2.1_classes.html").readText()
			val s2 = CombatCategory.render()
			val s3 = File(inputDir, "2.2_classes.html").readText()
			val s4 = classes.values.joinToString("\n") { it.render() }
			return "$s1\n$s2$s3\n$s4"
		}

	}
}