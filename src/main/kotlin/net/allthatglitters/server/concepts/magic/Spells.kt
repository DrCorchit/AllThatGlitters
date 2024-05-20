package net.allthatglitters.server.concepts.magic

import com.google.gson.JsonParser
import net.allthatglitters.server.util.Collapsible
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.inputDir
import java.io.File

class Spells(private val spellsDir: File = File(inputDir, "spells")) :
    HtmlFile("Appendix: Spells", "appendix_spells.html") {

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
            append(HtmlObject("h4").withContent("Schools of Sorcery"))
            append(HtmlObject("a").withAttribute("id", "top"))
            val ol = HtmlObject("ul")
            School.entries.forEach {
                val link = HtmlObject("a").withAttribute("href", "#${it.name}").withContent(it.name)
                ol.withContent(HtmlObject("li").withContent(link))
            }
            append(ol)

            getGroupedSpells().forEach {
                append(
                    HtmlObject("h4")
                        .withContent(
                            HtmlObject("a")
                                .withAttribute("id", it.key.name)
                        )
                        .withContent("School of ${it.key.name}")
                )
                it.value.forEach { spell ->
                    try {
                        append(spell.render())
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Error rendering spell ${spell.name}", e)
                    }
                }
                append(HtmlObject("a").withAttribute("href", "#top").withContent("Back to Top"))
            }
            append(Collapsible.render())
        } else {
            println("Warning: spellsDir is not a directory ($spellsDir)")
        }
        return this
    }

    fun showSpellStatistics() {
        val spellStatistics = StringBuilder()
        spellStatistics.append("              10  11  12  13  14  15  16  17  18  19  20 TOTAL")

        getGroupedSpells().forEach {
            spellStatistics.append(String.format("\n%-12s", it.key.name))
            val reqCount = mutableMapOf<Int, Int>()
            it.value.forEach { spell ->
                val key = it.key.primaryAttr.fullName
                val req = spell.trainingReqs[key] as Int
                reqCount[req] = reqCount.computeIfAbsent(req) { 0 } + 1
            }
            for (i in 10..20) {
                spellStatistics.append("%4s".format(reqCount.getOrDefault(i, 0)))
            }
            spellStatistics.append("%6s".format(reqCount.values.sum()))

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