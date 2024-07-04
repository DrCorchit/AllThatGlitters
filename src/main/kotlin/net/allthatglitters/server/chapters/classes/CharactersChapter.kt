package net.allthatglitters.server.chapters.classes

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.JsonParser
import net.allthatglitters.server.Generator
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import java.io.File

object CharactersChapter : HtmlFile(
	"How do I create a character?",
	"c2.html",
	File(generator.inputDir, "chapters/2_characters")
) {
	override val scripts = listOf("collapsible.js")
	override val templatizer = generator.templatizer.extend()
		.withRule("races") {
			races.values.joinToString("\n") { it.render() }
		}
		.withRule("sizes") {
			val table = HtmlObject("table")
			val headers = HtmlObject("tr")
				.withContent("th", "Size")
				.withContent("th", "Maximum Height")
				.withContent("th", "Grid Size")
				.withContent("th", "Terminal Velocity")
				.withContent("th", "Notes")

			table.withContent(headers)
				.withAll(Size.entries.map { it.toRow() }).render()
		}
		.withRule("combat_categories") { CombatCategory.render() }
		.withRule("combat_classes") {
			classes.values.joinToString("\n") { it.render() }
		}

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
		addSubsection("Races")
		addSubsection("Classes")
		addSubsection("Alignments", "alignments")
		addSubsection("Writing a Backstory", "backstory")
		addSubsection("Starting Equipment", "equipment")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		//Can't use getOutline() because we want to show the subsection names differently at the top of the chapter

		val altNames = listOf(
			"Choose A Race",
			"Choose a Class",
			"Pick an Alignment",
			"Write a Backstory",
			"Choose starting Equipment",
		)
		check(altNames.size == subsections.size) { "Subsection/Name mismatch detected in CharactersChapter!" }
		val list = HtmlObject("ol").withAll(
			subsections.zip(altNames).map {
				val section = it.first
				val title = it.second
				HtmlObject("li").withContent(section.linkTo(title))
			})
		append(list)
		subsections.forEach { appendSubsection(it) }
		appendElement(
			"p",
			"Once you've completed steps 1-5, you're now ready to fill out your character sheet. This is covered in the next chapter."
		)
		return this
	}
}