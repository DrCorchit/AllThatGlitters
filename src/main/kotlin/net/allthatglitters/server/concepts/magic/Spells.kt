package net.allthatglitters.server.concepts.magic

import com.google.gson.JsonParser
import net.allthatglitters.server.util.Collapsible
import net.allthatglitters.server.util.HtmlFile
import net.allthatglitters.server.util.HtmlObject
import net.allthatglitters.server.util.inputDir
import java.io.File

class Spells(private val spellsDir: File) : HtmlFile("Appendix: Spells", "appendix_spells.html") {

    fun getSpells(): List<Spell> {
        return spellsDir.listFiles()!!.flatMap { getSpells(it) }
    }

    fun getGroupedSpells(): Map<School, List<Spell>> {
        return getSpells().groupBy { it.discipline.school }
            //Sort keys and values alphabetically
            .toSortedMap()
            .mapValues { it.value.sortedBy { spell -> spell.name } }
    }

    override fun appendBody(): HtmlFile {
        if (spellsDir.isDirectory) {
            getGroupedSpells().forEach {
                append(HtmlObject("h4").withContent("School of ${it.key.name}").render())
                it.value.forEach { spell ->
                    try {
                        append(spell.render())
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Error rendering spell ${spell.name}", e)
                    }
                }
            }
            return append(Collapsible.render("collapsible-2", "active-2"))
        } else {
            println("Warning: spellsDir is not a directory ($spellsDir)")
            return this
        }
    }

    fun showSpellStatistics() {
        val spellStatistics = StringBuilder()
        spellStatistics.append("              10  11  12  13  14  15  16  17  18  19  20")

        getGroupedSpells().forEach {
            spellStatistics.append(String.format("\n%-12s", it.key.name))
            val reqCount = mutableMapOf<Int, Int>()
            it.value.forEach { spell ->
                val key = it.key.primaryAttr.name.lowercase()
                val req = spell.trainingReqs[key] as Int
                reqCount[req] = reqCount.computeIfAbsent(req) { 0 } + 1
            }
            for (i in 10..20) {
                spellStatistics.append(String.format("%4s", reqCount.getOrDefault(i, 0)))
            }
        }

        println(spellStatistics)
    }

    companion object {

        @JvmStatic
        fun main(vararg args: String) {
            Spells(File(inputDir, "spells")).showSpellStatistics()
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
}