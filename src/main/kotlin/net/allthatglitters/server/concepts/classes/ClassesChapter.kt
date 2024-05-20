package net.allthatglitters.server.concepts.classes

import com.google.gson.JsonParser
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.inputDir
import java.io.File

class ClassesChapter(val characterDir: File = File(inputDir,"characters")) :
    HtmlFile("How do I Create a Character?", "characters.html") {

        fun getClasses(): List<CombatClass> {
            return File(characterDir, "classes")
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
        if (characterDir.isDirectory) {
            append(File(characterDir, "0_intro.html").readText())
            append(File(characterDir, "1_choosing_a_race.html").readText())
            append(File(characterDir, "2.1_choosing_a_class.html").readText())
            append(CombatCategory.render())
            append(File(characterDir, "2.2_choosing_a_class.html").readText())
            getClasses().forEach { append(it.render()) }
            append(File(characterDir, "3_choosing_an_alignment.html").readText())
            append(File(characterDir, "4_writing_a_backstory.html").readText())
            append(File(characterDir, "5_choosing_starting_equipment.html").readText())
            append(File(characterDir, "6_character_sheet.html").readText())
        } else {
            println("Warning: characterDir is not a directory ($characterDir)")
        }
        return this
    }
}