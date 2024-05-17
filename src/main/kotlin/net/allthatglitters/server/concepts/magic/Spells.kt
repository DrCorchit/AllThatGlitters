package net.allthatglitters.server.concepts.magic

import com.google.gson.JsonParser
import net.allthatglitters.server.util.Collapsible
import net.allthatglitters.server.util.HtmlFile
import net.allthatglitters.server.util.HtmlObject
import java.io.File

class Spells(private val spellsDir: File) : HtmlFile("Appendix: Spells", "appendix_spells.html") {
    override fun appendBody(): HtmlFile {
        if (spellsDir.isDirectory) {
            val spells = spellsDir.listFiles()!!.flatMap { getSpells(it) }

            spells.groupBy { it.discipline.school }
                .mapValues { it.value.sortedBy { spell -> spell.name } }
                .forEach {
                    val reqCount = mutableMapOf<Int, Int>()

                    val school = it.key
                    append(HtmlObject("h4").withContent("School of ${school.name}").render())
                    it.value.forEach { spell ->
                        try {
                            val key = school.primaryAttr.name.lowercase()
                            val req = spell.trainingReqs[key]!!.asInt
                            reqCount[req] = reqCount.computeIfAbsent(req) { 0 } + 1
                            append(spell.render())
                        } catch (e: Exception) {
                            throw IllegalArgumentException("Error rendering spell ${spell.name}", e)
                        }
                    }
                    //println(reqCount)
                }
            return append(Collapsible.render())
        } else {
            println("Warning: spellsDir is not a directory ($spellsDir)")
            return this
        }
    }

    fun getSpells(file: File): List<Spell> {
        val json = file.readText()
            .let { JsonParser.parseString(it) }
            .asJsonArray

        return json.map {
            try {
                Spell.deserialize(it.asJsonObject)
            } catch (e: Exception) {
                val name = it.asJsonObject.get("name").asString
                throw IllegalArgumentException("Error parsing spell \"$name\"", e)
            }
        }
    }
}