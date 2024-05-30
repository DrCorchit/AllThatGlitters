package net.allthatglitters.server

import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import net.allthatglitters.server.appendices.abilities.AppendixTraining
import net.allthatglitters.server.appendices.armor.AppendixArmor
import net.allthatglitters.server.appendices.bestiary.AppendixBestiary
import net.allthatglitters.server.appendices.bestiary.Phylum
import net.allthatglitters.server.appendices.items.AppendixItems
import net.allthatglitters.server.appendices.magic.AppendixSpells
import net.allthatglitters.server.appendices.weapons.AppendixWeapons
import net.allthatglitters.server.chapters.classes.CharactersChapter
import net.allthatglitters.server.chapters.combat.CombatChapter
import net.allthatglitters.server.chapters.intro.IntroChapter
import net.allthatglitters.server.chapters.leveling.LevelingChapter
import net.allthatglitters.server.chapters.magic.MagicChapter
import net.allthatglitters.server.chapters.noncombat.NonCombatChapter
import net.allthatglitters.server.chapters.sheet.Sheet
import net.allthatglitters.server.chapters.sheet.SheetChapter
import net.allthatglitters.server.util.Collapsible
import net.allthatglitters.server.util.Navigation
import net.allthatglitters.server.util.Templatizer
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object Generator {
	val version = "0"
	val versionedOutputDir = File(Server.serviceDir, "version/$version")
	val inputDir = File("src/main/resources/input")
	val imagesDir = File(Server.serviceDir, "images")

	val deserializer: Gson
	val templatizer = Templatizer.getDefault()

	init {
		val builder = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
		builder.registerTypeAdapter(
			Phylum::class.java,
			JsonDeserializer { json, _, _ -> Phylum(json.asJsonObject) })
		deserializer = builder.create()
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
		IntroChapter,
		CharactersChapter,
		SheetChapter,
		LevelingChapter,
		CombatChapter,
		NonCombatChapter,
		MagicChapter
	)

	val files = (appendices + chapters).associateBy { it.fileName }

	fun lookupFile(file: String): HtmlFile {
		val key = file.normalize() + ".html"
		return files[key] ?: throw NoSuchElementException("No file named \"$file.html\"")
	}

	@JvmStatic
	fun main(vararg args: String) {
		imagesDir.mkdir()
		File(inputDir, "images").listFiles()!!.forEach {
			it.copyTo(File(imagesDir, it.name), true)
		}
		println("Copied images")

		File(inputDir, "styles.css")
			.copyTo(File(Server.serviceDir, "styles.css"), true)
		println("Copied styles.css")

		File(Sheet.inputDir, "styles.css")
			.copyTo(File(versionedOutputDir, "sheet/styles.css"), true)
		println("Copied sheet/styles.css")
		Sheet.appendHeader().appendBody().save()

		HtmlFile("All That Glitters", "index.html")
			.appendHeader()
			.appendTitle("h1")
			.appendBody()
			.save(File(Server.serviceDir, "index.html"))

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
		chapters[i - 1].appendHeader()
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

