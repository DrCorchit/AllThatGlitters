package net.allthatglitters.server

import com.google.gson.*
import net.allthatglitters.server.concepts.abilities.AppendixTraining
import net.allthatglitters.server.concepts.armor.AppendixArmor
import net.allthatglitters.server.concepts.bestiary.AppendixBestiary
import net.allthatglitters.server.concepts.bestiary.Phylum
import net.allthatglitters.server.concepts.items.AppendixItems
import net.allthatglitters.server.concepts.classes.CharactersChapter
import net.allthatglitters.server.concepts.magic.AppendixSpells
import net.allthatglitters.server.concepts.sheet.Sheet
import net.allthatglitters.server.concepts.sheet.SheetChapter
import net.allthatglitters.server.concepts.weapons.AppendixWeapons
import net.allthatglitters.server.util.Collapsible
import net.allthatglitters.server.util.Navigation
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object Generator {
	val version = "0"
	val outputDir = Server.serviceDir
	val versionedOutputDir = File(outputDir, "version/$version")
	val inputDir = File("src/main/resources/input")

	val deserializer: Gson

	init {
		val builder = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
		builder.registerTypeAdapter(
			Phylum::class.java,
			JsonDeserializer { json, _, _ -> Phylum(json.asJsonObject) })
		deserializer = builder.create()

		//Ensures ch3 stuff is loaded before later stuff that depends on it.
		SheetChapter.attributes.forEach { (abbr, attr) -> println("$abbr -> ${attr.name}") }
	}

	val appendices = listOf(
		AppendixSpells,
		AppendixTraining,
		AppendixWeapons,
		AppendixArmor,
		AppendixItems,
		AppendixBestiary
	)

	val chapters = listOf(
		HtmlFile("How do I play the game?", "c1.html"),
		CharactersChapter,
		SheetChapter,
		//HtmlFile("What's on my character sheet?", "c3.html"),
		HtmlFile("How does my character become stronger?", "c4.html"),
		HtmlFile("How does my character fight?", "c5.html"),
		HtmlFile("What can my character do besides fighting?", "c6.html"),
		HtmlFile("How does my character use magic?", "c7.html")
	)

	@JvmStatic
	fun main(vararg args: String) {
		File(inputDir, "styles.css")
			.copyTo(File(outputDir, "styles.css"), true)
		println("Copied styles.css")

		File(Sheet.inputDir, "styles.css")
			.copyTo(File(versionedOutputDir, "sheet/styles.css"), true)
		println("Copied sheet/styles.css")
		Sheet.appendHeader().appendBody().save()

		HtmlFile("All That Glitters", "index.html")
			.appendHeader()
			.appendTitle("h1")
			.appendBody()
			.save()

		HtmlFile("Player's Handbook", "phb_toc.html")
			.appendHeader()
			.appendElement("h1", "All That Glitters")
			.appendTitle("h2")
			.appendBody()
			.save()

		for (i in 1..chapters.size) makeChapter(i)
		for (i in appendices.indices) makeAppendix(i)
	}

	fun makeChapter(i: Int) {
		val nav = Navigation.forChapter(i)
		chapters[i-1].appendHeader()
			.appendElement("h2", "Chapter $i")
			.appendTitle().append(nav)
			.appendBody().append(nav)
			.append(Collapsible)
			.save()
	}

	fun makeAppendix(i: Int) {
		val prev = if (i > 0) {
			appendices[i - 1]
		} else null
		val next = if (i < appendices.size - 1) {
			appendices[i + 1]
		} else null
		val nav = Navigation(
			prev?.let { it.title to it.fileName },
			next?.let { it.title to it.fileName })

		appendices[i]
			.appendHeader()
			.appendTitle().append(nav)
			.appendBody().append(nav)
			.save()
	}
}

