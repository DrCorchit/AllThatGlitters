package net.allthatglitters.server.concepts.magic

import net.allthatglitters.server.Generator.inputDir
import net.allthatglitters.server.util.Collapsible
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import java.io.File

object AppendixSpells : HtmlFile("Appendix: Spells", "appendix_spells.html") {
	val spellsDir: File = File(inputDir, "spells")
	val spells = spellsDir.listFiles()!!
		.flatMap { it.deserialize { obj -> Spell.deserialize(obj) } }

	fun getGroupedSpells(): Map<School, List<Spell>> {
		return spells.groupBy { it.discipline.school }
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

	@JvmStatic
	fun main(vararg args: String) {
		showSpellStatistics()
	}
}