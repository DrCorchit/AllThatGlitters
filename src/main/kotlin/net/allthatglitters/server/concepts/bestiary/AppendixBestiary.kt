package net.allthatglitters.server.concepts.bestiary

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.italicise
import java.io.File

object AppendixBestiary : HtmlFile("Appendix: Bestiary", "appendix_bestiary.html") {
	override val inputDir = File(Generator.inputDir, "creatures")
	val planes = File(inputDir, "planes.json")
		.deserialize { Plane.deserialize(it) }
	val spirits = File(inputDir, "spirits.json")
		.deserialize { Spirit.deserialize(it) }

	val phyla = File(inputDir, "categories/phyla.json")
		.deserialize { Phylum(it) }
		.associateBy { it.latinName.lowercase() }

	fun lookupPhylum(name: String): Phylum {
		try {
			return phyla[name.lowercase()]!!
		} catch (e: NullPointerException) {
			throw NoSuchElementException("No phylum named \"$name\"")
		}
	}

	val genera = File(inputDir, "categories/genera.json")
		.deserialize { Genus(lookupPhylum(it.get("phylum").asString), it) }
		.associateBy { it.latinName.lowercase() }

	fun lookupGenus(name: String): Genus {
		return genera[name.lowercase()]!!
	}

	val creatures = File(inputDir, "animals.json")
		.deserialize { Creature(it) }

	override fun appendBody(): HtmlFile {
		appendElement("h4", "The Planes of Existence")
		append(HtmlObject("p").withContent("The world consists of the following planes:"))
		planes.forEach { appendElement("p", it.description) }

		appendElement("h4", "The Spirits of the Universe")
		spirits.forEach {
			val str = "${it.singular} (${it.latin.italicise()}): ${it.description})"
			appendElement("p", str)
		}

		appendElement("h4", "The Categories of Monster and Beast")
		phyla.values.forEach {
			val div = HtmlObject.background()
			div.withContent("h5", it.commonNamePlural)
			div.withContent("p", it.description)
			div.withContent("p", "Subcategories:")
			it.genera.forEach { phylum ->
				val p = HtmlObject("p").withStyle("margin-left: 25px;")
					.withContent(phylum.commonNamePlural.bold())
					.withContent(": ${phylum.description}")
				div.withContent(p)
			}
			append(div)
		}
		return this
	}
}