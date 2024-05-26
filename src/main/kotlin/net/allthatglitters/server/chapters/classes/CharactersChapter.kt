package net.allthatglitters.server.chapters.classes

import com.google.gson.JsonParser
import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object CharactersChapter : HtmlFile("How do I create a character?", "c2.html") {
	override val inputDir: File = File(Generator.inputDir, "chapters/2_characters")
	fun getClasses(): List<CombatClass> {
		return File(inputDir, "classes")
			.listFiles()!!.map {
				try {
					val json = JsonParser.parseString(it.readText())
					CombatClass.deserialize(json.asJsonObject)
				} catch (e: Exception) {
					throw IllegalArgumentException("Error parsing class info for ${it.name}", e)
				}
			}.sortedBy { it.name }
	}

	override fun appendBody(): HtmlFile {
		if (inputDir.isDirectory) {
			append(File(inputDir, "0_intro.html").readText())
			append(File(inputDir, "1_choosing_a_race.html").readText())
			append(File(inputDir, "2.1_choosing_a_class.html").readText())
			append(CombatCategory)
			append(File(inputDir, "2.2_choosing_a_class.html").readText())
			getClasses().forEach { append(it) }
			append(File(inputDir, "3_choosing_an_alignment.html").readText())
			append(File(inputDir, "4_writing_a_backstory.html").readText())
			append(File(inputDir, "5_choosing_starting_equipment.html").readText())
			append(File(inputDir, "6_character_sheet.html").readText())
		} else {
			println("Warning: characterDir is not a directory ($inputDir)")
		}
		return this
	}
}