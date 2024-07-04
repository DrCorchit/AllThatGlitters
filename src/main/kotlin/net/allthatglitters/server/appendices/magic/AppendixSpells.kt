package net.allthatglitters.server.appendices.magic

import com.drcorchit.justice.utils.StringUtils.normalize
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.concepts.requirement.AttrReq
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import java.io.File

object AppendixSpells : HtmlFile(
	"Appendix: Spells",
	"appendix_spells.html",
	File(generator.inputDir, "appendices/1_spells")
) {
	override val scripts = listOf("collapsible.js", "clipboard.js")

	val spells = inputDir.listFiles()!!
		.flatMap { spellFile -> spellFile.deserialize { Spell.deserialize(it) } }
		.associateBy { it.name.normalize() }

	fun getGroupedSpells(): Map<School, List<Spell>> {
		return spells.values.groupBy { it.college.school }
			//Sort keys and values alphabetically
			.toSortedMap()
			.mapValues { it.value.sortedBy { spell -> spell.name } }
	}

	fun lookupSpell(str: String): Spell {
		return spells[str.normalize()] ?: throw NoSuchElementException("No such spell: $str")
	}

	override fun appendBody(): HtmlFile {
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
			it.value.sortedBy { spell -> spell.level }.forEach { spell ->
				try {
					append(spell.render())
				} catch (e: Exception) {
					throw IllegalArgumentException("Error rendering spell ${spell.name}", e)
				}
			}
			append(HtmlObject("a").withAttribute("href", "#top").withContent("Back to Top"))
		}
		return this
	}

	fun showSpellStatistics() {
		val spellStatistics = StringBuilder()
		spellStatistics
			.append("\n")
			.append("              10  11  12  13  14  15  16  17  18  19  20 TOTAL")

		var total = 0

		getGroupedSpells().forEach { school ->
			total += school.value.size
			spellStatistics.append(String.format("\n%-12s", school.key.name))
			val reqCount = mutableMapOf<Int, Int>()
			school.value.forEach { spell ->
				val key = school.key.primaryAttr
				val req = spell.trainingReqs.filterIsInstance<AttrReq>()
					.find { req -> req.attribute == key }?.minLevel ?: 0
				reqCount[req] = reqCount.computeIfAbsent(req) { 0 } + 1
			}
			for (i in 10..20) {
				spellStatistics.append("%4s".format(reqCount.getOrDefault(i, 0)))
			}
			spellStatistics.append("%6s".format(reqCount.values.sum()))
		}
		spellStatistics.append("\nTOTAL: $total")

		logger.info(spellStatistics.toString())
	}

	fun printSpells() {
		spells.values.filter {
			it.college.school == School.Alchemy
		}.forEach {
			println(it.descriptiveString())
		}
	}

	@JvmStatic
	fun main(vararg args: String) {
		showSpellStatistics()
		//printSpells()
	}
}