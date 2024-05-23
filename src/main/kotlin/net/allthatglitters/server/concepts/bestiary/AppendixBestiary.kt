package net.allthatglitters.server.concepts.bestiary

import net.allthatglitters.server.Generator.inputDir
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.italicise
import java.io.File

object AppendixBestiary : HtmlFile("Appendix: Bestiary", "appendix_bestiary.html") {
	val creaturesDir = File(inputDir, "creatures")
	val planes = File(creaturesDir, "planes.json")
		.deserialize { Plane.deserialize(it) }
	val spirits = File(creaturesDir, "spirits.json")
		.deserialize { Spirit.deserialize(it) }

	val kingdoms = File(creaturesDir, "categories/phyla.json")
		.deserialize { Phylum(it) }
		.associateBy { it.latin.lowercase() }

	val phyla = kingdoms.values.flatMap { it.genera }
		.associateBy { it.latin.lowercase() }

	fun lookupKingdom(name: String): Phylum {
		return kingdoms[name.lowercase()]!!
	}

	fun lookupPhylum(name: String): Genus {
		return phyla[name.lowercase()]!!
	}

	val creatures = File(creaturesDir, "animals.json")
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
		kingdoms.values.forEach {
			val div = HtmlObject.background()
			div.withContent("h5", it.plural)
			div.withContent("p", it.description)
			div.withContent("p", "Subcategories:")
			it.genera.forEach { phylum ->
				val p = HtmlObject("p").withStyle("margin-left: 25px;")
					.withContent(phylum.plural.bold())
					.withContent(": ${phylum.description}")
				div.withContent(p)
			}
			append(div)
		}
		return this
	}
}